package ekaiser.nzlov.util;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Properties;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/**
* 其实JDK里面也有支持FTP操作的包【jre/lib下的rt.jar】，但是SUN的DOC里面并没有提供相应文档，
* 因为这里面的包，不被官方支持，建议不要使用。我们可以使用第三方提供的包apache.commons。
* apache.commons的包，都有文档，方便使用
* 另外IBM也有提供一个ftp包，我没有用过，有兴趣的可以去研究一下
* @commons-net：http://apache.mirror.phpchina.com/commons/net/binaries/commons-net-1.4.1.zip
* @jakarta-oro：http://mirror.vmmatrix.net/apache/jakarta/oro/source/jakarta-oro-2.0.8.zip
* @commons-io：http://apache.mirror.phpchina.com/commons/io/binaries/commons-io-1.3.2-bin.zip
* @author 我行我素
* @2007-08-03
*/
public class MiniFtp {
    private static String username;
    private static String password;
    private static String ip;
    private static int port;
    private static Properties property=null;//配置
    private static String configFile;//配置文件的路径名
   
    private static FTPClient ftpClient=null;
    private static SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm");
   
    private static final String [] FILE_TYPES={"文件","目录","符号链接","未知类型"};
   
    public static void main(String[] args) {
         setConfigFile("FTP.ini");//设置配置文件路径
         connectServer();
         listAllRemoteFiles();//列出所有文件和目录
         changeWorkingDirectory("user/123/book");//进入文件夹webroot
        // listRemoteFiles("*.jsp");//列出webroot目录下所有jsp文件
         setFileType(FTP.BINARY_FILE_TYPE);//设置传输二进制文件
         //uploadFile("woxingwosu.xml","myfile.xml");//上传文件woxingwosu.xml，重新命名为myfile.xml
        // renameFile("viewDetail.jsp", "newName.jsp");//将文件viewDetail.jsp改名为newName.jsp
        // deleteFile("UpdateData.class");//删除文件UpdateData.class
        // loadFile("UpdateData.java","loadFile.java");//下载文件UpdateData.java，并且重新命名为loadFile.java
         uploadFile("FTP.ini","FTP.a");
         closeConnect();//关闭连接
     }
   
    /**
      * 上传文件
      * @param localFilePath--本地文件路径
      * @param newFileName--新的文件名
     */
    public static void uploadFile(String localFilePath,String newFileName){
         connectServer();
        //上传文件
         BufferedInputStream buffIn=null;
        try{
             buffIn=new BufferedInputStream(new FileInputStream(localFilePath));
             ftpClient.storeFile(newFileName, buffIn);
         }catch(Exception e){
             e.printStackTrace();
         }finally{
            try{
                if(buffIn!=null)
                     buffIn.close();
             }catch(Exception e){
                 e.printStackTrace();
             }
         }
     }
   
    /**
      * 下载文件
      * @param remoteFileName --服务器上的文件名
      * @param localFileName--本地文件名
     */
    public static void loadFile(String remoteFileName,String localFileName){
         connectServer();
        //下载文件
         BufferedOutputStream buffOut=null;
        try{
             buffOut=new BufferedOutputStream(new FileOutputStream(localFileName));
             ftpClient.retrieveFile(remoteFileName, buffOut);
         }catch(Exception e){
             e.printStackTrace();
         }finally{
            try{
                if(buffOut!=null)
                     buffOut.close();
             }catch(Exception e){
                 e.printStackTrace();
             }
         }
     }
   
    /**
      * 列出服务器上所有文件及目录
     */
    public static void listAllRemoteFiles(){
         listRemoteFiles("*");
     }

    /**
      * 列出服务器上文件和目录
      * @param regStr --匹配的正则表达式
     */
    public static void listRemoteFiles(String regStr){
         connectServer();
        try{
             FTPFile[] files=ftpClient.listFiles(regStr);
            if(files==null||files.length==0)
                 System.out.println("There has not any file!");
            else{
                 TreeSet<FTPFile> fileTree=new TreeSet(
                        new Comparator(){
                            //先按照文件的类型排序(倒排)，然后按文件名顺序排序
                            public int compare(Object objFile1,Object objFile2){
                                if(objFile1==null)
                                    return -1;
                                else if(objFile2==null)
                                    return 1;
                                else{
                                     FTPFile file1=(FTPFile)objFile1;
                                     FTPFile file2=(FTPFile)objFile2;
                                    if(file1.getType()!=file2.getType())
                                        return file2.getType()-file1.getType();
                                    else
                                        return file1.getName().compareTo(file2.getName());
                                 }
                             }
                         }
                 );
                for(FTPFile file:files)
                     fileTree.add(file);
                 System.out.printf("%-35s%-10s%15s%15s\n","名称","类型","修改日期","大小");
                for(FTPFile file:fileTree){
                     System.out.printf("%-35s%-10s%15s%15s\n",iso8859togbk(file.getName()),FILE_TYPES[file.getType()]
                             ,dateFormat.format(file.getTimestamp().getTime()),FileUtils.byteCountToDisplaySize(file.getSize()));
                 }
             }
         }catch(Exception e){
             e.printStackTrace();
         }
     }
   
    /**
      * 关闭连接
     */
    public static void closeConnect(){
        try{
            if(ftpClient!=null){
                 ftpClient.logout();
                 ftpClient.disconnect();
                 ftpClient = null;
             }
         }catch(Exception e){
             e.printStackTrace();
         }
     }
   
    /**
      * 设置配置文件
      * @param configFile
     */
    public static void setConfigFile(String configFile) {
         MiniFtp.configFile = configFile;
     }
   
    /**
      * 设置传输文件的类型[文本文件或者二进制文件]
      * @param fileType--BINARY_FILE_TYPE、ASCII_FILE_TYPE
     */
    public static void setFileType(int fileType){
        try{
             connectServer();
             ftpClient.setFileType(fileType);
         }catch(Exception e){
             e.printStackTrace();
         }
     }
   
    /**
      * 扩展使用
      * @return
     */
    protected static FTPClient getFtpClient(){
         connectServer();
        return ftpClient;
     }

    /**
      * 设置参数
      * @param configFile --参数的配置文件
     */
    private static void setArg(String configFile){
         property=new Properties();
         BufferedInputStream inBuff=null;
        try{
             inBuff=new BufferedInputStream(new FileInputStream(configFile));
             property.load(inBuff);
             username=property.getProperty("username");
             password=property.getProperty("password");
             ip=property.getProperty("ip");
             port=Integer.parseInt(property.getProperty("port"));
         }catch(Exception e){
             e.printStackTrace();
         }finally{
            try{
                if(inBuff!=null)
                     inBuff.close();
             }catch(Exception e){
                 e.printStackTrace();
             }
         }
     }
   
    /**
      * 连接到服务器
     */
    public static void connectServer() {
        if (ftpClient == null) {
            int reply;
            try {
                 setArg(configFile);
                 ftpClient=new FTPClient();
                 ftpClient.setDefaultPort(port);
                 ftpClient.configure(getFtpConfig());
                 ftpClient.connect(ip);
                 System.out.println("ip:"+ip+":"+port+" u:"+username+" p:"+password);
                 ftpClient.login(username, password);
                 ftpClient.setDefaultPort(port);
                 System.out.print(ftpClient.getReplyString());
                 reply = ftpClient.getReplyCode();

                if (!FTPReply.isPositiveCompletion(reply)) {
                     ftpClient.disconnect();
                     System.err.println("FTP server refused connection.");
                 }
             } catch (Exception e) {
                 System.err.println("登录ftp服务器【"+ip+"】失败");
                 e.printStackTrace();
             }
         }
     }
   
    /**
      * 进入到服务器的某个目录下
      * @param directory
     */
    public static void changeWorkingDirectory(String directory){
        try{
             connectServer();
             ftpClient.changeWorkingDirectory(directory);
         }catch(IOException ioe){
             ioe.printStackTrace();
         }
     }
   
    /**
      * 返回到上一层目录
     */
    public static void changeToParentDirectory(){
        try{
             connectServer();
             ftpClient.changeToParentDirectory();
         }catch(IOException ioe){
             ioe.printStackTrace();
         }
     }
   
    /**
      * 删除文件
     */
    public static void deleteFile(String filename){
        try{
             connectServer();
             ftpClient.deleteFile(filename);
         }catch(IOException ioe){
             ioe.printStackTrace();
         }
     }
   
    /**
      * 重命名文件
      * @param oldFileName --原文件名
      * @param newFileName --新文件名
     */
    public static void renameFile(String oldFileName,String newFileName){
        try{
             connectServer();
             ftpClient.rename(oldFileName, newFileName);
         }catch(IOException ioe){
             ioe.printStackTrace();
         }
     }
   
    /**
      * 设置FTP客服端的配置--一般可以不设置
      * @return
     */
    private static FTPClientConfig getFtpConfig(){
         FTPClientConfig ftpConfig=new FTPClientConfig(FTPClientConfig.SYST_UNIX);
         ftpConfig.setServerLanguageCode(FTP.DEFAULT_CONTROL_ENCODING);
        return ftpConfig;
     }
   
    /**
      * 转码[ISO-8859-1 ->   GBK]
      *不同的平台需要不同的转码
      * @param obj
      * @return
     */
    private static String iso8859togbk(Object obj){
        try{
            if(obj==null)
                return "";
            else
                return new String(obj.toString().getBytes("iso-8859-1"),"GBK");
         }catch(Exception e){
            return "";
         }
     }

} 