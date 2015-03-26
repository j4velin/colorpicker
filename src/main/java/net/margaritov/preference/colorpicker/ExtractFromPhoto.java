package net.margaritov.preference.colorpicker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

public class ExtractFromPhoto extends Activity {

    private final static int REQUEST_IMAGE_CAPTURE = 1;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<Palette.Swatch> swatches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            setTitle(R.string.press_color_to_apply);
            setContentView(R.layout.extract);
            mRecyclerView = (RecyclerView) findViewById(R.id.grid);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
            mAdapter = new ColorsAdapter();
            mRecyclerView.setAdapter(mAdapter);
        } else {
            Toast.makeText(this, R.string.camera_not_found, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                Palette p = Palette.generate(imageBitmap, 16);
                swatches = p.getSwatches();
                mAdapter.notifyDataSetChanged();
            } else {
                finish();
            }
        }
    }

    private class ColorsAdapter extends RecyclerView.Adapter<ColorsAdapter.ColorHolder> implements
            View.OnClickListener {

        @Override
        public ColorHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = new View(ExtractFromPhoto.this);
            int size = (int) Util.dpToPx(ExtractFromPhoto.this, 50);
            v.setLayoutParams(new RecyclerView.LayoutParams(size, size));
            ColorHolder h = new ColorHolder(v);
            h.v.setOnClickListener(this);
            return h;
        }

        @Override
        public void onBindViewHolder(ColorHolder holder, int position) {
            int color = swatches.get(position).getRgb();
            holder.v.setBackgroundColor(color);
            holder.v.setTag(color);
        }

        @Override
        public int getItemCount() {
            return swatches.size();
        }

        @Override
        public void onClick(View v) {
            ColorPickerDialog.mListener.onColorChanged((int) v.getTag());
            finish();
        }

        public class ColorHolder extends RecyclerView.ViewHolder {
            private final View v;

            public ColorHolder(final View itemView) {
                super(itemView);
                v = itemView;
            }
        }
    }
}
