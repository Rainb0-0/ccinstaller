import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

class Installer {

	public static void main(String[] args) {
		new Window();
	}

}

class Window implements ActionListener {
	private static JFrame main;

	private static ButtonGroup versionSelector;
	private static JLabel version;
	private static JRadioButton versionSelector1_8;
	private static JRadioButton versionSelector1_7;
	private static String selectedVersion;

	private static JButton pathSelectorButton;
	private static JFileChooser pathSelector;
	private static File minecraftPath;

	Window() {
		main = new JFrame("Installer");
		main.setSize(350, 200);
		versionSelector = new ButtonGroup();

		version = new JLabel("Version : ");
		version.setLocation(10, 50);
		version.setSize(version.getMinimumSize());

		versionSelector1_8 = new JRadioButton("1.8.9");
		versionSelector1_8.setSize(versionSelector1_8.getMinimumSize());
		versionSelector1_8.setLocation((int) (main.getBounds().width * 0.3 - (versionSelector1_8.getSize().width / 2)),
				50);
		versionSelector1_8.addActionListener(this);

		versionSelector1_7 = new JRadioButton("1.7.10");
		versionSelector1_7.setSize(versionSelector1_7.getMinimumSize());
		versionSelector1_7.setLocation((int) (main.getBounds().width * 0.6 - (versionSelector1_7.getSize().width / 2)),
				50);
		versionSelector1_7.addActionListener(this);

		versionSelector.add(versionSelector1_7);
		versionSelector.add(versionSelector1_8);

		pathSelectorButton = new JButton("Select .minecraft folder");
		pathSelectorButton.setSize(200, 30);
		pathSelectorButton.setLocation((int) (main.getBounds().width * 0.5 - (pathSelectorButton.getSize().width / 2)),
				100);
		pathSelectorButton.setEnabled(false);
		pathSelectorButton.addActionListener(this);

		main.add(versionSelector1_8);
		main.add(versionSelector1_7);
		main.add(version);
		main.add(pathSelectorButton);
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.setResizable(true);
		main.setLocationRelativeTo(null); // center window on screen
		main.setLayout(null);
		main.setResizable(false);
		main.setVisible(true);
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == versionSelector1_8) {
			selectedVersion = "1.8";
			enablePathSelectorButton();
		} else if (event.getSource() == versionSelector1_7) {
			selectedVersion = "1.7";
			enablePathSelectorButton();
		} else if (event.getSource() == pathSelectorButton) {
			pathSelector = new JFileChooser();
			pathSelector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			pathSelector.setFileHidingEnabled(false);
			selectPath();

		}
	}

	public void enablePathSelectorButton() {
		if (pathSelectorButton.isEnabled() == false) {
			pathSelectorButton.setEnabled(true);
		}
	}

	public void selectPath() {
		if (pathSelector.showDialog(null, "Select .minecraft folder") == JFileChooser.APPROVE_OPTION) {
			minecraftPath = pathSelector.getSelectedFile();
			checkPath();
		}
	}

	public void checkPath() {
		File versionsFolder = new File(minecraftPath, "versions");
		if (!minecraftPath.toString().contains(".minecraft") || !versionsFolder.exists()) {
			JOptionPane.showMessageDialog(new JFrame(), "Not a valid folder", "Dialog", JOptionPane.ERROR_MESSAGE);
		} else {
			Forge checkForge = new Forge();
			checkForge.setMinecraftPath(versionsFolder);
			checkForge.setVersion(selectedVersion);
			if (checkForge.checkForge()) {
				JOptionPane.showMessageDialog(new JFrame(), "You already have forge installed for that version. installing cloud client", "Dialog", JOptionPane.DEFAULT_OPTION);
				install();
			}
			else if (!checkForge.checkForge()){
				checkForge.install();
			}
		}
	}
	public void install(){
		clientInstall ci = new clientInstall();
		ci.setVersion(selectedVersion);
		ci.setMinecraftPath(minecraftPath.toPath());
		ci.install();
	}
	public void print(String arg) {
		System.out.println(arg);
	}
}

class Forge {
	private static final String forgeURL1_7 = "https://maven.minecraftforge.net/net/minecraftforge/forge/1.7.10-10.13.4.1614-1.7.10/forge-1.7.10-10.13.4.1614-1.7.10-installer.jar";
	private static final String forgeURL1_8 = "https://maven.minecraftforge.net/net/minecraftforge/forge/1.8.9-11.15.1.2318-1.8.9/forge-1.8.9-11.15.1.2318-1.8.9-installer.jar";
	private static final String forgeName1_8 = "forge-1.8.9-11.15.1.2318-1.8.9-installer.jar";
	private static final String forgeName1_7 = "forge-1.7.10-10.13.4.1614-1.7.10-installer.jar";
	private static String forgeVersion;
	private static File[] versionsDir;

	public void setMinecraftPath(File path) {
		versionsDir = path.listFiles(File::isDirectory);
	}

	public void setVersion(String version) {
		forgeVersion = version;
	}

	public boolean checkForge() {
		for (int i = 0; i < versionsDir.length; i++) {
			if (versionsDir[i].toString().toLowerCase().contains("forge")
					&& versionsDir[i].toString().toLowerCase().contains(forgeVersion)) {
				return true;
			}
		}
		return false;
	}

	public void install() {
		download();
	}

	private void download() {
		if (forgeVersion.contains("1.7")) {
			removeOld();
			try (InputStream in = (new URL(forgeURL1_7)).openStream()) {
				String filePath = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator")
						+ forgeName1_7;
				Files.copy(in, Paths.get(filePath));
				JOptionPane.showMessageDialog(new JFrame(), "Downloaded Forge, now running it. Re-run installation after forge finished installing", "Dialog",
						JOptionPane.NO_OPTION);
				run(filePath);
				System.exit(0);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(new JFrame(), "Could not download Forge file!");
			}
		} else if (forgeVersion.contains("1.8")) {
			removeOld();
			try (InputStream in = (new URL(forgeURL1_8)).openStream()) {
				String filePath = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator")
						+ forgeName1_8;
				Files.copy(in, Paths.get(filePath));
				JOptionPane.showMessageDialog(new JFrame(), "Downloaded Forge, now running it. Re-run installation after forge finished installing", "Dialog",
						JOptionPane.NO_OPTION);
				run(filePath);
				System.exit(0);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(new JFrame(), "Could not download Forge file!");
			}
		}
	}

	private void run(String path) {
		if (forgeVersion.contains("1.8")){
			ProcessBuilder pb = new ProcessBuilder("java", "-jar", System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + forgeName1_8);
			try {
				pb.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (forgeVersion.contains("1.7")){
			ProcessBuilder pb = new ProcessBuilder("java", "-jar", System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + forgeName1_7);
			try {
				pb.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void removeOld() {
		File file1_8 = Paths
				.get(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + forgeName1_8)
				.toFile();
		File file1_7 = Paths
				.get(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + forgeName1_7)
				.toFile();
		if (file1_8.exists() || file1_7.exists()) {
			file1_7.delete();
			file1_8.delete();
		}
	}
}

class clientInstall {
	private static final String modURL1_7 = "https://cloudmc.dev/download/cloud-1.7.jar";
	private static final String modURL1_8 = "https://cloudmc.dev/download/cloud-1.8.jar";
	private static final String modName1_7 = "cloud-1.7.jar";
	private static final String modName1_8 = "cloud-1.8.jar";
	private static String clientVersion;
	private static Path minecraftPath;
	public void setVersion(String version){
		clientVersion = version;
	}
	public void setMinecraftPath(Path path){
		minecraftPath = path;
	}
	public void install(){
		createModsFolder();
		download();
	}
	public void download(){
		if (clientVersion.contains("1.7")){
			String filePath = minecraftPath.toString() + System.getProperty("file.separator") + "mods" + System.getProperty("file.separator") + modName1_7;
			try (InputStream in = (new URL(modURL1_7)).openStream()) {
				Files.copy(in, Paths.get(filePath));
				JOptionPane.showMessageDialog(new JFrame(), "Cloud client installed!", "Dialog",
						JOptionPane.NO_OPTION);
				System.exit(0);
			} catch (IOException e) {
				if (Paths.get(filePath).toFile().exists()){
					JOptionPane.showMessageDialog(new JFrame(), "File already in mods folder!", "Dialog", JOptionPane.NO_OPTION);
				}
				else {
					JOptionPane.showMessageDialog(new JFrame(), "Could not download file!");
				}
			}
		}
		if (clientVersion.contains("1.8")){
			String filePath = minecraftPath.toString() + System.getProperty("file.separator") + "mods" + System.getProperty("file.separator") + modName1_8;
			try (InputStream in = (new URL(modURL1_8)).openStream()) {
				Files.copy(in, Paths.get(filePath));
				JOptionPane.showMessageDialog(new JFrame(), "Cloud client installed!", "Dialog",
						JOptionPane.NO_OPTION);
				System.exit(0);
			} catch (IOException e) {
				if (Paths.get(filePath).toFile().exists()){         
					JOptionPane.showMessageDialog(new JFrame(), "File already in mods folder!" , "Dialog" , JOptionPane.NO_OPTION);
				}
				else {
					JOptionPane.showMessageDialog(new JFrame(), "Could not download file!");
				}
			}
		}
	}
	public void createModsFolder(){
		File modsFolder = Paths.get(minecraftPath.toString() + System.getProperty("file.separator") + "mods").toFile();
		if (!modsFolder.exists()){
			new File(minecraftPath + System.getProperty("file.separator") + "mods").mkdir();
			System.out.print("lolll");
		}
	}
}
