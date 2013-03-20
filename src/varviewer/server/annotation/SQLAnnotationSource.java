package varviewer.server.annotation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import varviewer.shared.Variant;

public class SQLAnnotationSource implements AnnotationProvider {

	String sqlUsername;
	String sqlPassword;
	String dbName;
	
	Connection conn = null;
	
	public void initializeConnection() throws SQLException {
		if (getSqlUsername() == null || getSqlPassword()==null) {
			throw new IllegalStateException("No account credentials provided");
		}
		
	    Properties connectionProps = new Properties();
	    connectionProps.put("user", getSqlUsername());
	    connectionProps.put("password", getSqlPassword());
	    
	
		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", connectionProps);
	}
	
	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getSqlUsername() {
		return sqlUsername;
	}

	public void setSqlUsername(String sqlUsername) {
		this.sqlUsername = sqlUsername;
	}

	public String getSqlPassword() {
		return sqlPassword;
	}

	public void setSqlPassword(String sqlPassword) {
		this.sqlPassword = sqlPassword;
	}

	@Override
	public Collection<String> getAnnotationsProvided() {
		if (conn == null) {
			try {
				initializeConnection();
			} catch (SQLException e) {
				Logger.getLogger(getClass()).error("Could not initialize SQL connection : "  + e.getMessage());
				e.printStackTrace();
			}
		}
		
		try {

			//Execute a quick bogus query to get a result set
			String query = "SELECT * FROM " + getDbName() + ".varChr12 " + " where pos = 1";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData meta = rs.getMetaData();
			
			List<String> columnNames = new ArrayList<String>();
			for(int i=1; i<=meta.getColumnCount(); i++) {
				columnNames.add(meta.getColumnLabel(i));
			}
			return columnNames;
		} catch (SQLException e) {
			Logger.getLogger(getClass()).error("SQL error reading column names from database " + getDbName() + " : "  + e.getMessage());
			e.printStackTrace();
		}

		
		return null;
	}

	@Override
	public AnnotationKeyIndex[] getKeyIndices(List<String> annotations) {
		if (conn == null) {
			try {
				initializeConnection();
			} catch (SQLException e) {
				Logger.getLogger(getClass()).error("Could not initialize SQL connection : "  + e.getMessage());
				e.printStackTrace();
			}
		}
		
		String query = "SELECT * FROM " + getDbName() + ".varChr12" + " where pos = 1";
		Statement stmt;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData meta = rs.getMetaData();
			AnnotationKeyIndex[] indices = new AnnotationKeyIndex[meta.getColumnCount()];
			for(int i=1; i<=meta.getColumnCount(); i++) {
				//columnNames.add(meta.getColumnLabel(i));
//				System.out.println("Column name: " + meta.getColumnLabel(i) + "\t" + meta.getColumnType(i) + "\t" + meta.getColumnTypeName(i));
				String typeLabel = meta.getColumnTypeName(i);
				boolean numeric = false;
				if (typeLabel.contains("INT") || typeLabel.contains("DECIMAL")) {
					numeric = true;
				}
				AnnotationKeyIndex keyIndex = new SQLAnnotationKey(meta.getColumnLabel(i), i, numeric);
				indices[i-1] = keyIndex;
			}
			return indices;
			
		} catch (SQLException e) {
			Logger.getLogger(getClass()).error("Error retrieving SQL annotation keys : "  + e.getMessage());
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public void annotateVariant(Variant var, AnnotationKeyIndex[] annotationKeys) {		
		try {
			if (conn == null) {
				initializeConnection();
			}	

			Statement stmt = null;
			String query = "SELECT * FROM " + getDbName() + ".varChr" + var.getChrom() + " " + " where pos = " + var.getPos() + " and alt=\"" + var.getAlt() + "\"";
		
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				for(int i=0; i<annotationKeys.length; i++) {
					if (annotationKeys[i].isNumeric()) {
						Double anno = rs.getDouble(annotationKeys[i].getIndex());
						var.addAnnotation(annotationKeys[i].getKey(), anno);
					}
					else {
						String anno = rs.getString(annotationKeys[i].getIndex());
						var.addAnnotation(annotationKeys[i].getKey(), anno);
					}
				}

			}
		} catch (SQLException e ) {
			System.err.println("uh oh: " + e.toString());
		} 	
	}

	class SQLAnnotationKey implements AnnotationKeyIndex {

		final String key;
		final int index;
		final boolean isNumeric;
		
		public SQLAnnotationKey(String key, int index, boolean numeric) {
			this.key = key;
			this.index = index;
			this.isNumeric = numeric;
		}
		
		@Override
		public String getKey() {
			return key;
		}

		@Override
		public int getIndex() {
			return index;
		}

		@Override
		public boolean isNumeric() {
			return isNumeric;
		}
		
	}
}
