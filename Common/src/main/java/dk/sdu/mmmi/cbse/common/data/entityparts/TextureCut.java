package dk.sdu.mmmi.cbse.common.data.entityparts;

/**
 *
 * @author Group 7
 */
public class TextureCut {

	private int x;
	private int y;
	private int width;
	private int height;
	
	private float offsetX;
	private float offsetY;

	public TextureCut(int width, int height, int x, int y, float offsetX, float offsetY) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public float getOffsetX() {
		return offsetX;
	}

	public float getOffsetY() {
		return offsetY;
	}
	
}
