VarViewer
=========

GWT viewer for NGS variant files


### Installation, Building & Deployment

The big dependency of this app is [Google Web Toolkit](http://www.gwtproject.org/) (GWT) v 2.6.0. There's a great GWT [plugin for eclipse](https://developers.google.com/eclipse/index) (only compatible with Eclipse Luna, for now). Before you do anything, you'll need to install the plugin.

 Once you've installed the plugin and cloned the repo you'll need to do to more things:
  1. Grab the user / password file from research (genseqar01 `/usr/share/tomcat6/webapps/VarViewer/WEB-INF/classes/user.details.props`) and drop it into the repo (and the `war/` directory, I believe)
  2. Modify the [`spring.xml`](https://github.com/ARUP-NGS/VarViewer/blob/master/src/spring.xml) file to refer to local resources. The most important part is to point it to a local folder containing some review directories to play with. Do this by modifying this part:

    
  `<property name="rootDir" value="/home/brendan/jobwrangler_samples" />`
	   
just replace the `/home/brendan/...` part with the path to the folder you want to work with. 

Once these items are in place, just click the big green Run button in Eclipse to start the webapp. You'll get a choice to run as a "Web Application", "Web Application (Classic Dev Mode)", "Web Application (Super Dev Mode)". Choose "Super Dev Mode". Once the app starts it'll get you an address that looks something like `http://localhost:8888/VarViewer.html`. Pop that into a browser and you should be good to go.

If you're unable to log in, it's probably because VarViewer can't find the `user.details.props` password file. Try moving it into the war/ folder or the war/WEB-INF/classes/ folder.



