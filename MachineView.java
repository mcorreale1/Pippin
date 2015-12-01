package pippin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Observable;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

public class MachineView extends Observable {
	private MachineModel model;
	private String defaultDir;
	private String sourceDir;
	private String executableDir;
	private Properties properties = null;
	private CodeViewPanel codeViewPanel;
    private MemoryViewPanel memoryViewPanel1;
    private MemoryViewPanel memoryViewPanel2;
    private MemoryViewPanel memoryViewPanel3;
    private ControlPanel controlPanel;
    private ProcessorViewPanel processorPanel;
    private MenuBarBuilder menuBuilder;
    private JFrame frame;

	public int getChangedIndex() {
		return model.getChangedIndex();
	}

	public int getData(int index) {
		return model.getData(index);
	}
	
	public int getProgramCounter() {
		return model.getProgramCounter();
	}
	
	public int getAccumulator() {
		return model.getAccumulator();
	}

	public MachineView(MachineModel model) {
		this.model = model;
		locateDefaultDirectory();
		loadPropertiesFile();
		createAndShowGUI();
	}
	
	private void locateDefaultDirectory() {
		//CODE TO DISCOVER THE ECLIPSE DEFAULT DIRECTORY:
		File temp = new File("propertyfile.txt");
		if(!temp.exists()) {
			PrintWriter out;
			try {
				out = new PrintWriter(temp);
				out.close();
				defaultDir = temp.getAbsolutePath();
				temp.delete();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			defaultDir = temp.getAbsolutePath();
		}
		// change to forward slashes
		defaultDir = defaultDir.replace('\\','/');
		int lastSlash = defaultDir.lastIndexOf('/');
		defaultDir  = defaultDir.substring(0, lastSlash + 1);
	}
	
	private void loadPropertiesFile() {
		try { // load properties file "propertyfile.txt", if it exists
			properties = new Properties();
			properties.load(new FileInputStream("propertyfile.txt"));
			sourceDir = properties.getProperty("SourceDirectory");
			executableDir = properties.getProperty("ExecutableDirectory");
			// CLEAN UP ANY ERRORS IN WHAT IS STORED:
			if (sourceDir == null || sourceDir.length() == 0 
					|| !new File(sourceDir).exists()) {
				sourceDir = defaultDir;
			}
			if (executableDir == null || executableDir.length() == 0 
					|| !new File(executableDir).exists()) {
				executableDir = defaultDir;
			}
		} catch (Exception e) {
			// PROPERTIES FILE DID NOT EXIST
			sourceDir = defaultDir;
			executableDir = defaultDir;
		}		
	}
	
	/**
	 * Method that sets up the whole GUI and locates the individual
	 * components into place. Also sets up the Menu bar. Starts a 
	 * swing timer ticking.
	 */
	private void createAndShowGUI() {
		codeViewPanel = new CodeViewPanel(this);
		memoryViewPanel1 = new MemoryViewPanel(this, 0, 160);
		memoryViewPanel2 = new MemoryViewPanel(this, 160, 240);
		memoryViewPanel3 = new MemoryViewPanel(this, 240, Memory.DATA_SIZE);
		controlPanel = new ControlPanel(this);
		//processorPanel = new ProcessorViewPanel(this);
		processorPanel = new ProcessorViewPanel();
		menuBuilder = new MenuBarBuilder(this);
		frame = new JFrame("Pippin Simulator");
		Container content = frame.getContentPane();
		content.setLayout(new BorderLayout(1,1));
		content.setBackground(Color.BLACK);
		frame.setSize(1200,600);
		JPanel center = new JPanel();
		center.setLayout(new GridLayout(1,3));

		frame.add(codeViewPanel.createCodeDisplay(),BorderLayout.LINE_START);
		frame.add(center,BorderLayout.CENTER);
		center.add(memoryViewPanel1.createMemoryDisplay());
		center.add(memoryViewPanel2.createMemoryDisplay());
		center.add(memoryViewPanel3.createMemoryDisplay());
		frame.add(controlPanel.createControlDisplay(),BorderLayout.PAGE_END);
		frame.add(processorPanel.createProcessorDisplay(),BorderLayout.PAGE_START);

		JMenuBar bar = new JMenuBar();
		frame.setJMenuBar(bar);
		bar.add(menuBuilder.createFileMenu());
		bar.add(menuBuilder.createExecuteMenu());

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(null); // needs work
//		state = States.NOTHING_LOADED;
//		state.enter();
		setChanged();
		notifyObservers();
//		timer = new Timer(TICK, needs lambda);
//		timer.start();
		frame.setVisible(true);
	}
	
	 /**
     * Main method that drives the whole simulator
     * @param args command line arguments are not used
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MachineView(new MachineModel()); 
            }
        });
    }  
}
