package cysabiork.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import cysabiork.restful.SabioRKQuery;


import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class SabioRKDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField serverField;
	private JTextField queryField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			SabioRKDialog dialog = new SabioRKDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void openDialog() {
		try {
			SabioRKDialog dialog = new SabioRKDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Create the dialog.
	 */
	public SabioRKDialog(JFrame pParent) {
		
		// General settings
		super(pParent, true);
		this.setSize(600, 400);
		this.setResizable(false);
		this.setLocationRelativeTo(pParent);
		setTitle("SabioRK Restful Interface");
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			serverField = new JTextField();
			serverField.setText(SabioRKQuery.SABIORK_RESTFUL_URL);
			serverField.setBounds(26, 32, 306, 30);
			contentPanel.add(serverField);
			serverField.setColumns(10);
		}
		
		JLabel lblSabioRestfulServer = new JLabel("SabioRK REST Server");
		lblSabioRestfulServer.setBounds(26, 12, 157, 15);
		contentPanel.add(lblSabioRestfulServer);
		
		JLabel lblExamples = new JLabel("Examples");
		lblExamples.setBounds(26, 132, 70, 15);
		contentPanel.add(lblExamples);
		
		final JList exampleList = new JList();
		exampleList.addListSelectionListener(new ListSelectionListener() {
			
			// Set query from example list if selected
			public void valueChanged(ListSelectionEvent arg0) {
				if (!exampleList.isSelectionEmpty()){
					String example = (String) exampleList.getSelectedValue();
					queryField.setText(example);
				}
			}
		});
		
		exampleList.setModel(new AbstractListModel() {
			String[] values = new String[] {
					"/kineticLaws/18974", 
					"/kineticLaws?kinlawids=18974,18976,18975,22516",
					"/searchKineticLaws/sbml?q=Tissue:liver%20AND%20Organism:Homo%20sapiens%20AND%20Substrate:Glucose",
					"/searchKineticLaws/sbml?q=ReactantChebi:17925"
					
			};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		exampleList.setBounds(28, 153, 529, 150);
		contentPanel.add(exampleList);
		
		JLabel lblSabioRestQuery = new JLabel("Sabio REST Query");
		lblSabioRestQuery.setBounds(26, 74, 157, 15);
		contentPanel.add(lblSabioRestQuery);
		
		queryField = new JTextField();
		queryField.setText("/kineticLaws/18974");
		queryField.setColumns(10);
		queryField.setBounds(26, 90, 531, 30);
		contentPanel.add(queryField);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton loadButton = new JButton("Load");
				loadButton.setActionCommand("OK");
				buttonPane.add(loadButton);
				getRootPane().setDefaultButton(loadButton);
				loadButton.addActionListener(new ActionListenerLoadButton());
			}
			{
				final JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListenerCancelButton());
				buttonPane.add(cancelButton);
			}
		}
	}
	
	class ActionListenerCancelButton implements ActionListener{
		public void actionPerformed(ActionEvent arg0) {
			dispose();
		}
	}
	class ActionListenerLoadButton implements ActionListener{
		public void actionPerformed(ActionEvent arg0) {
			performSabioRKQuery();
		}
	}
	
	public String getQueryURL(){
		String queryURL = serverField.getText() + queryField.getText();
		return queryURL;
	}
	
	public void performSabioRKQuery(){
		String queryURL = getQueryURL();
		System.out.println("CySabioRK[INFO] > GET " + queryURL);
		SabioRKQuery query = new SabioRKQuery();
		String xml = query.performQuery(queryURL);
	}
}
