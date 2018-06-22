package volunteer.sk.greate43.com.gotcha;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Aspire v5-573G on 4/2/2018.
 */

public class profile_fragment extends Fragment {
    private static final String Tag ="profle_fragment";
    Button logout;
    Intent intent;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.profile_fragment,container,false);

        logout = (Button) view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(getActivity(),Login.class);
                startActivity(intent);
                onDestroy();
            }
        });
        return view;
    }

}

