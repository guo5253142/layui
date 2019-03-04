package layui;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

/**
 * svg格式转换成图片
 * @author guopeng1
 *
 */
public class SvgToImgUtl {
 
	public static void saveImg(String svgCode,String pngFilePath){
		try {
			convertToPng(svgCode.toString(), pngFilePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
 
	/**
	 * 将svg字符串转换为png
	 * 
	 * @param svgCode
	 *            svg代码
	 * @param pngFilePath
	 *            保存的路径
	 * @throws TranscoderException
	 *             svg代码异常
	 * @throws IOException
	 *             io错误
	 */
	public static void convertToPng(String svgCode, String pngFilePath) {
 
		File file = new File(pngFilePath);
 
		FileOutputStream outputStream = null;
		try {
			file.createNewFile();
			outputStream = new FileOutputStream(file);
			convertToPng(svgCode, outputStream);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
 
	/**
	 * 将svgCode转换成png文件，直接输出到流中
	 * 
	 * @param svgCode
	 *            svg代码
	 * @param outputStream
	 *            输出流
	 * @throws TranscoderException
	 *             异常
	 * @throws IOException
	 *             io异常
	 */
	public static void convertToPng(String svgCode, OutputStream outputStream) {
		try {
			byte[] bytes = svgCode.getBytes("utf-8");
			PNGTranscoder t = new PNGTranscoder();
			TranscoderInput input = new TranscoderInput(
					new ByteArrayInputStream(bytes));
			TranscoderOutput output = new TranscoderOutput(outputStream);
			t.transcode(input, output);
			outputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
 

}
