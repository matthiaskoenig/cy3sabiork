# cy2sabiork: SabioRK integration in Cytoscape 2
<div align="right>
<a href="https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&amp;hosted_button_id=RYHNRJFBMWD5N" title="Donate to this project using Paypal"><img src="https://img.shields.io/badge/paypal-donate-yellow.svg" alt="PayPal donate button" /></a>
</div>

**cy2sabiork** is a [Cytoscape 2](http://www.cytoscape.org) plugin for the for accessing kinetic information from
[SABIO-RK](http://sabio.villa-bosch.de/) via the RESTful web service.  

[![Download](docs/images/icon-download.png) Download](https://github.com/matthiaskoenig/cy2sabiork/releases/latest)  
**Documentation**: http://matthiaskoenig.github.io/cy2sabiork/  
**Support & Forum**: https://groups.google.com/forum/#!forum/cysbml-cyfluxviz  
**Bug Tracker**: https://github.com/matthiaskoenig/cy2sabiork/issues  

## Features
- Accessing SABIO-RK data via search interface in Cytoscape

[![alt tag](docs/images/logo-sabiork.png)](http://sabio.villa-bosch.de/)  

## License
* Source Code: [GPLv3](http://opensource.org/licenses/GPL-3.0)
* Documentation: [CC BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)

## Install
* install Cytoscape v2.8.3  
  http://www.cytoscape.org/download.html
* download latest versions of cy2sbml and cy2sabiork from  
https://github.com/matthiaskoenig/cy2sbml/releases/latest  
https://github.com/matthiaskoenig/cy2sabiork/releases/latest
* move the downloaded jar files  
`cy2sbml-vx.xx.jar`  
`cy2sabiork-vx.xx.jar`  
in the Cytoscape plugin folder
`Cytoscape_v2.8.*/plugins/`
* remove `sbml-reader-2.8.3-jar-with-dependencies.jar` from the plugin folder.

**cy2sabiork** is installed and available in Cytoscape under plugins after the next startup of Cytoscape.

## Uninstall
* remove `cy2sabiork-vx.xx.jar` from the plugin folder

## Build instructions
Clone the repository and build with `ant`
```
git clone https://github.com/matthiaskoenig/cy2sabiork.git cy2sabiork
cd cy2sabiork
ant cy2sabiork
```
Development is done in `develop` branch, documentation in `gh-pages`
```
git checkout -b develop --track origin/develop
git checkout -b gh-pages --track origin/gh-pages
```

# Change Log
**v0.2.0** [2015-10-27]
- migration to github
- cy2sbml-v1.4.0

**v0.17**
- logger updated 

**v0.16**
- update library dependecies -> jersey-1.17.1

**v0.15**
- first working version
