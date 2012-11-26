package ekaiser.nzlov.theme;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;



public class ThemeDialog extends JDialog{

	private JList themeList;
	private JButton use;
	private JButton add;
	private JButton del;
	private JButton close;
	private String oTheme;
	private ThemeManage controller;
	private boolean isLoad;
	private boolean isUse;
	public ThemeDialog(ThemeManage controller){
		this.controller = controller;
		init();
		regeListener();
	}
	/**
	 * װ��
	 */
	private void init(){
		JPanel north = new JPanel();
		JLabel t = new JLabel("主题列表：");
			t.setPreferredSize(new Dimension(50,25));
			t.setBounds(10, 0, 80, 25);
		north.setLayout(null);
		north.add(t);
		north.setPreferredSize(new Dimension(0,25));
		
	
		JPanel west = new JPanel();
		west.setPreferredSize(new Dimension(10,0));		
	
		JPanel center = new JPanel();
			themeList = new JList();
			themeList.setDragEnabled(false);//关闭拖放
			themeList.setFixedCellHeight(35);//固定单元格高度
			themeList.setFixedCellWidth(200);//固定单元格宽度
			themeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			JPanel ceast = new JPanel();
				use = new JButton("应用");
				add = new JButton("添加");
				del = new JButton("删除");
				close = new JButton("取消");
			ceast.setLayout(new BoxLayout(ceast, BoxLayout.Y_AXIS));
			ceast.add(use);
			ceast.add(Box.createVerticalStrut(10));
			ceast.add(add);
			ceast.add(Box.createVerticalStrut(10));
			ceast.add(del);
			ceast.add(Box.createVerticalStrut(10));
			ceast.add(close);
			ceast.setPreferredSize(new Dimension(60,0));
		center.setLayout(new BorderLayout());
		center.add(new JScrollPane(themeList),BorderLayout.WEST);
		center.add(ceast,BorderLayout.EAST);
		
		
		JPanel east = new JPanel();
		east.setPreferredSize(new Dimension(10,0));
		
	
		JPanel south = new JPanel();
		south.setPreferredSize(new Dimension(0,10));
		
		setTitle("主题管理");
		setLayout(new BorderLayout());
		//setAlwaysOnTop(true);
		setResizable(false);
		setBounds(120, 260, 320,350);
		setLocationRelativeTo(null);
		//setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		add(north,BorderLayout.NORTH);
		add(west,BorderLayout.WEST);
		add(center,BorderLayout.CENTER);
		add(east,BorderLayout.EAST);
		add(south,BorderLayout.SOUTH);
	}
	private void regeListener(){
		this.add.addActionListener(new addAction());
		this.del.addActionListener(new delAction());
		this.close.addActionListener(new closeAction());
		this.use.addActionListener(new useAction());
		this.themeList.addListSelectionListener(new listSelect());
		this.addWindowListener(new windowsClose());
	}
	public void load(){
		this.isLoad = true;
		this.themeList.setListData(this.controller.getThemeList());
		this.oTheme = this.controller.getTheme();
		this.isLoad = false;
	}
	private void add(){
		this.controller.add();
	}
	private void del(){
		String str =(String) themeList.getSelectedValue();
		this.controller.del(str);
	}
	private void use(boolean isNew){
		if(isNew){
			String str =(String) themeList.getSelectedValue();
			this.controller.setTheme(str);
		}else{
			this.controller.setTheme(this.oTheme);
			this.setVisible(false);
		}
	}
	//=========================================各种监听==================================
	private class addAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			add();
		}
	}
	private class delAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			del();
		}
	}
	private class closeAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			use(false);
			
		}
	}
	private  class useAction implements ActionListener{
		public void actionPerformed(ActionEvent e){
			isUse = true;
			setVisible(false);
		}
	}
	private class listSelect implements ListSelectionListener{
		public void valueChanged(ListSelectionEvent e){
			if(!isLoad)
				use(true);
		}
	}
	private class windowsClose extends WindowAdapter{
		public void windowClosed(WindowEvent e){
			if(!isUse)
				use(false);
		}
	}
}
