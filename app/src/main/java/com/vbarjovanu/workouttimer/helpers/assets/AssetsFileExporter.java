package com.vbarjovanu.workouttimer.helpers.assets;

import android.content.Context;

import com.vbarjovanu.workouttimer.helpers.files.BytesFileWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * exports the content of an android asset file to a local file
 */
public class AssetsFileExporter {
    private Context context;

    public AssetsFileExporter(Context context) {
        this.context = context;
    }

    /**
     * exports the content of an android asset file to a local file
     * @param assetPath path of the android asset
     * @param exportFilePath destination file path
     * @throws IOException
     */
    public void exportAsset(String assetPath, String exportFilePath) throws IOException {
        byte[] content = this.readAssetContent(assetPath);
        BytesFileWriter fileWriter = new BytesFileWriter();
        fileWriter.writeFile(exportFilePath, content);
    }

    /**
     * Reads and returns as byte array the content of an android app asset
     * @param assetPath path of the android asset
     * @return byte array
     * @throws IOException when asset path is not found
     */
    private byte[] readAssetContent(String assetPath) throws IOException {
        Context ctx = this.context;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        InputStream inputStream = ctx.getResources().getAssets().open(assetPath);
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        return buffer.toByteArray();
    }
}
