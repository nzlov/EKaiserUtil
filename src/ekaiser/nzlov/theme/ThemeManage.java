package ekaiser.nzlov.theme;

import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.nilo.plaf.nimrod.NimRODLookAndFeel;
import com.nilo.plaf.nimrod.NimRODTheme;

import ekaiser.nzlov.util.MyFileFilter;



public class ThemeManage {
	private NimRODTheme nt;
	private NimRODLookAndFeel nf;
	private MyFileFilter filter;
	private String theme;
	private ThemeDialog view;
	private ArrayList<Component> frameList = null;
	public ThemeManage() {
		this.view = new ThemeDialog(this); 
		filter  = new MyFileFilter(false);
		filter.addFilter(".qptheme", "主题文件(*.qptheme)");
		frameList = new ArrayList<Component>();
		frameList.add(this.view);
	}
	public void setTheme(String str){
		this.theme = str;
		nt = new NimRODTheme("theme/" + str);
		nf = new NimRODLookAndFeel();
		nf.setCurrentTheme(nt);
		try{
			UIManager.setLookAndFeel(nf);
			//SwingUtilities.updateComponentTreeUI(mainFrame.getContentPane());
			for(Component c:frameList)
				SwingUtilities.updateComponentTreeUI(c);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public String getTheme(){
		return this.theme;
	}
	public Vector getThemeList(){
		Vector vector = new Vector();
		File[] files = ((new File("theme/")).listFiles());
		for(File f:files){
			if(filter.accept(f)){
				vector.add(f.getName());
			}
		}
		return vector;
	}
	public void del(String str){
		(new File("theme/"+str)).delete();
		show();
	}
	public void add(){
		JFileChooser jf = new JFileChooser(".");
		jf.setFileFilter(filter);
		jf.setToolTipText("添加主题");
		jf.setAcceptAllFileFilterUsed(false);
		int i =jf.showOpenDialog(this.view);
		if(i == jf.APPROVE_OPTION){
			File fd = jf.getSelectedFile();
			FileInputStream fis=null;
			FileOutputStream fos=null;
			FileChannel fcin=null;
			FileChannel fcout=null;
				try{
						fis =  new FileInputStream(fd.getPath());
						fos = new FileOutputStream("theme/"+fd.getName());
						fcin = fis.getChannel();
						fcout = fos.getChannel();
						fcin.transferTo(0,fcin.size(),fcout);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					try{
				        fcin.close();
				        fcout.close();
				        fis.close();
				        fos.close();
					}catch(Exception a){}
				}
		}
	}
	public void show(){
		this.view.load();
		this.view.setVisible(true);
	}
	public void addView(Component c){
		frameList.add(c);
		setTheme(theme);
	}
}
