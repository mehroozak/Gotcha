package volunteer.sk.greate43.com.gotcha;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.zip.InflaterInputStream;

/**
 * Created by Aspire v5-573G on 4/2/2018.
 */

public class gallery_fragment extends Fragment {
    private static final String Tag ="gallery_fragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.gallery_fragment,container,false);
        return view;
    }
}

