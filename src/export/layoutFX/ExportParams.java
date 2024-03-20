package export.layoutFX;

import java.io.Serializable;


/**
 * Parameter for the exporter. 
 * @author Jamie Macaulay
 *
 */
public class ExportParams implements Serializable, Cloneable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The index of the ex;port choice. 
	 */
	public int exportChoice = 0; 
	
	
	@Override
	public ExportParams clone()  {
		try {
			ExportParams newP = (ExportParams) super.clone();
			return newP;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

}
