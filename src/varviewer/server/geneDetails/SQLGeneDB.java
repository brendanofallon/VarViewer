package varviewer.server.geneDetails;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import varviewer.shared.GeneInfo;

/**
 * Obtains geneDetails objects from a SQL data source. Right now this is pretty fragile and will 
 * throw an exception if certain properties are not found in the table. 
 * @author brendan
 *
 */
public class SQLGeneDB implements GeneDetailHandler {

	private JdbcTemplate jdbcTemplate = null;
	private String query = "SELECT * FROM geneInfo where gene=?";
	
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public GeneInfo getInfoForGene(String geneID) {		
		Mapper gMapper = new Mapper();
		try {
			Object obj = jdbcTemplate.queryForObject(query, new Object[]{ geneID }, gMapper);
			if (obj == null) {
				Logger.getLogger(getClass()).warn("Could not find gene info for gene id: " + geneID);
			}
			return obj == null 
					? null 
					: (GeneInfo) obj;
		}
		catch (DataAccessException ex) {
			Logger.getLogger(getClass()).warn("Error encountered accessing info for gene " + geneID + " :" + ex.getMessage() + ", " + ex.getMostSpecificCause());
			return null;
		}
	}


	class Mapper implements RowMapper<GeneInfo> {

		@Override
		public GeneInfo mapRow(ResultSet rs, int arg1) throws SQLException {
			GeneInfo info = new GeneInfo();
			info.setDbNSFPDisease( rs.getString("disease_description") );
			info.setSummary( rs.getString("summary"));
			info.setOmimInheritance( rs.getString("omim.inheritance").split(",") );
			info.setOmimPhenos( rs.getString("omim.phenotypes").split(",") );
			info.setOmimDiseaseIDs( rs.getString("omim.numbers").split(";"));
			info.setHgmdVars( rs.getString("hgmd.info").split(":") );
			info.setFullName( rs.getString("full_name") );
			return info;
		}
		
	}
}
