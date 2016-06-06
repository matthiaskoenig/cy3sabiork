# cy3sabiork: SABIO-RK for Cytoscape 3
<div align="right>
<a href="https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&amp;hosted_button_id=RYHNRJFBMWD5N" title="Donate to this project using Paypal"><img src="https://img.shields.io/badge/paypal-donate-yellow.svg" alt="PayPal donate button" /></a>
</div>

**cy3sabiork** is a [Cytoscape 3](http://www.cytoscape.org) app for accessing kinetic information from [SABIO-RK](http://sabio.villa-bosch.de/) via the RESTful web service.
  
[![alt tag](docs/images/logo-sabiork.png)](http://sabio.villa-bosch.de/)  

**Support & Forum**: https://groups.google.com/forum/#!forum/cysbml-cyfluxviz  
**Bug Tracker**: https://github.com/matthiaskoenig/cy3sabiork/issues  

## Features
- Accessing SABIO-RK data via search interface in Cytoscape

## License
* Source Code: [GPLv3](http://opensource.org/licenses/GPL-3.0)
* Documentation: [CC BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)

## Install
* install [Cytoscape v3.4.0](http://www.cytoscape.org/download.html) or higher
* install cy3sbml and cy3sabiork via the Cytoscape app store  
`Apps -> App Manager`

Alternatively
* download latest versions of [cy3sbml](https://github.com/matthiaskoenig/cy3sbml/releases/latest) and [cy3sabiork](https://github.com/matthiaskoenig/cy3sbml/releases/latest) and move the downloaded jar files  
`cy3sbml-vx.x.x.jar`  
`cy3sabiork-vx.x.x.jar`  
in the Cytoscape app folder
`CytoscapeConfiguration/3/apps/installed/`


## Build instructions
Clone the repository and build with `maven`
```
git clone https://github.com/matthiaskoenig/cy3sabiork.git
cd cy3sabiork
mvn clean install cy3sabiork
```
Development is done in `develop` branch, documentation in `gh-pages`
```
git checkout -b develop --track origin/develop
git checkout -b gh-pages --track origin/gh-pages
```

## Change Log
**v0.3.0** [?]
- ported to Cytoscape 3
- updated documentation

**v0.2.0** [2015-10-27]
- migration to github
- cy2sbml-v1.4.0

**v0.17**
- logger updated 

**v0.16**
- update library dependecies -> jersey-1.17.1

**v0.15**
- first working version
