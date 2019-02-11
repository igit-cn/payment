import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class Demo {

	public static void main(String[] args) throws IOException {
		String path = "C:\\Users\\11697\\eclipse-workspace\\mall";
		
		File file = new File(path);
		list(file);
	}


	private static void list(File file) throws IOException {
		if(file.isDirectory()) {
			File [] files = file.listFiles();
			for (File file2 : files) {
				if(file2.isDirectory()) {
					list(file2);
				}else {
					String fileName = file2.getName();
					if(fileName.endsWith("ftl")) {
						String newFileName=fileName.substring(0,fileName.indexOf("ftl"))+"html";
						File file3 = new File(file.getAbsolutePath()+"/"+newFileName);
						FileUtils.copyFile(file2, file3);
						FileUtils.deleteQuietly(file2);
					}
				}
			}
		}else {
			System.out.println(file.getAbsolutePath());
		}
	}
	
}
