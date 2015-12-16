package com.aeo.mylensesstudio.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.aeo.mylensesstudio.R;
import com.aeo.mylensesstudio.activity.TimeLensActivity;
import com.aeo.mylensesstudio.adapter.ListReplaceLensBaseAdapter;
import com.aeo.mylensesstudio.dao.LensDAO;
import com.aeo.mylensesstudio.vo.LensStatusVO;

import java.util.List;

public class FragmentListReplaceLens extends ListFragment {

    public static List<LensStatusVO> listLenses;
    ListReplaceLensBaseAdapter mListAdapter;
    public static final String TAG_LENS = "TAG_LENS";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        List<LensStatusVO> listLens = LensDAO.getInstance(getContext()).getListLens();

        if (listLens != null && listLens.size() == 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    inflater.getContext(),
                    android.R.layout.simple_list_item_1,
                    new String[]{getString(R.string.msg_insert_time_replace)});
            setListAdapter(adapter);
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        TextView idLens = (TextView) v.findViewById(R.id.textViewIdReplaceLens);

        if (idLens != null) {
            startActivity(Integer.valueOf(idLens.getText().toString()));
        }
    }

    private void startActivity(int idLens) {
        Intent intent = new Intent(getContext(), TimeLensActivity.class);
        intent.putExtra("ID_LENS", idLens);
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem menuItemInsert = menu.findItem(R.id.menuInsertLens);
        menuItemInsert.setVisible(true);

        MenuItem menuItemHelp = menu.findItem(R.id.menuHelp);
        menuItemHelp.setVisible(false);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuInsertLens:
                startActivity(new Intent(getContext(), TimeLensActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        List<LensStatusVO> listLens = LensDAO.getInstance(getContext()).getListLens();

        if (listLens != null && listLens.size() > 0) {
            mListAdapter = new ListReplaceLensBaseAdapter(getContext(), listLens);
            setListAdapter(mListAdapter);
        } else {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    inflater.getContext(),
                    android.R.layout.simple_list_item_1,
                    new String[]{getString(R.string.msg_insert_time_replace)});
            setListAdapter(adapter);
        }
    }
}
