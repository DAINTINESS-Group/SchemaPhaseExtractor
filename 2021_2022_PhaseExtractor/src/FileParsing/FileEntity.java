package FileParsing;

import java.io.File;

public class FileEntity 
{
	private File file;
	private FileType type;
	
	FileEntity(String path,FileType type)
	{
		this.file = new File(path);
		this.type = type;
	}
	
	public File getFile() 
	{
		return this.file;
	}
	
	public FileType getFileType() 
	{
		return this.type;
	}
	
	public String getFileTitle() 
	{
		String filename = this.file.getName();
		int typeIndex = filename.indexOf(".");
		return filename.substring(0, typeIndex);
	}
}
