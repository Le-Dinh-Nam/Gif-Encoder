package gifencoder.nakhon.com.gifencoder;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private Bitmap decodeBitmap(int resId, int width, int height){
        Bitmap bmp, ret;
        bmp = BitmapFactory.decodeResource(MainActivity.this.getResources(), resId);
        ret = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        RectF src = new RectF(0, 0, bmp.getWidth(), bmp.getHeight());
        RectF dst = new RectF(0, 0, width, height);

        Matrix transform = new Matrix();
        transform.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER);

        Canvas can = new Canvas(ret);

        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setFilterBitmap(true);

        can.drawBitmap(bmp, transform, mPaint);

        bmp.recycle();

        return ret;
    }

    public void startEncode(View v){
        final ProgressDialog progressBar = new ProgressDialog(MainActivity.this);
        progressBar.setMessage("Encoding....");
        progressBar.show();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int width = 400;
                int height = 500;
                Bitmap bmp1 = decodeBitmap(R.drawable.test0, width, height);
                Bitmap bmp2 = decodeBitmap(R.drawable.test1, width, height);

                Bitmap list[] = new Bitmap[4];
                list[0] = bmp1;
                list[1] = bmp2;
                list[2] = bmp1;
                list[3] = bmp2;

                Giffle encoder = new Giffle();

                final String outPath = "/sdcard/out.gif";

                encoder.encode(MainActivity.this, outPath, width, height, list, 500);

                bmp1.recycle();

                bmp2.recycle();

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GifImageView gifPreview = (GifImageView)findViewById(R.id.preview);
                        try {
                            GifDrawable drawable = new GifDrawable(outPath);

                            gifPreview.setImageDrawable(drawable);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        progressBar.dismiss();
                    }
                });
            }
        });

        thread.setName("encoding");
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }
}
