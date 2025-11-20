package fr.draconium.core.init.sounds;

public enum Format
{
	OGG(".ogg"),
	MP3(".mp3");

	private final String extension;

	Format(String extension)
	{
		this.extension = extension;
	}
	
	public String getExtension()
	{
		return this.extension;
	}
}
