package cysabiork;

/*
Copyright (c) 2015, Matthias Koenig, Computational Systems Biochemistry, 
Charite Berlin
matthias.koenig [at] charite.de

This library is free software; you can redistribute it and/or modify it
under the terms of the GNU Lesser General Public License as published
by the Free Software Foundation; either version 2.1 of the License, or
any later version.

This library is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
documentation provided hereunder is on an "as is" basis, and the
Institute for Systems Biology and the Whitehead Institute
have no obligations to provide maintenance, support,
updates, enhancements or modifications.  In no event shall the
Institute for Systems Biology and the Whitehead Institute
be liable to any party for direct, indirect, special,
incidental or consequential damages, including lost profits, arising
out of the use of this software and its documentation, even if the
Institute for Systems Biology and the Whitehead Institute
have been advised of the possibility of such damage.  See
the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this library; if not, write to the Free Software Foundation,
Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.swing.JFrame;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;

import cysabiork.gui.SabioRKDialog;
import cysbml.logging.LogCyPlugin;

/**
 * CySabioRK plugin. Display reaction kinetic information via WebServices
 * in Cytoscape.
 * 
 * Main Developer:
 * Matthias Koenig, Computational Systems Biochemistry, Charite Berlin
 * matthias.koenig [at] charite.de
 * 
 * @author Matthias Koenig
 * @date 2014-01-24 
 */

public class CySabioRKPlugin extends CytoscapePlugin implements PropertyChangeListener{
	
	public static final String NAME = "cy2sabiork"; 
	public static final String VERSION = "v0.2.0";
	public static LogCyPlugin LOGGER = new LogCyPlugin(NAME);
	
	public CySabioRKPlugin() throws SecurityException, IOException{	
		CySabioRKPlugin.LOGGER.info(getVersionedName());
		try {
			 CySabioRKAction action = new CySabioRKAction();
			 action.setPreferredMenu("Plugins");
			 Cytoscape.getDesktop().getCyMenus().addAction(action);
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static String getVersionedName(){
		return NAME + "-" + VERSION;
	}

    ///////////////// MENU ACTIONS ////////////////////////////////////

	@SuppressWarnings("serial")
	public class CySabioRKAction extends CytoscapeAction {
		
		/** The constructor sets the text that should appear on the menu item.*/
	    public CySabioRKAction() {super(NAME);}
	    
	    /** This method is called when the user selects the menu item.*/
	    public void actionPerformed(ActionEvent ae) {
		    SabioRKDialog sabioRKDialog = new SabioRKDialog((JFrame) Cytoscape.getDesktop());
		    sabioRKDialog.setVisible(true);
	    	//SabioRKDialog.openDialog();
	    }
	}
}
