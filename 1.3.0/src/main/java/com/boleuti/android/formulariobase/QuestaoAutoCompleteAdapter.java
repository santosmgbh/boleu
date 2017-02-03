package com.boleuti.android.formulariobase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;


import com.boleuti.android.formulariobase.model.ItemListaValor;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by almir.santos on 20/10/2014.
 */
public class QuestaoAutoCompleteAdapter extends ArrayAdapter implements Filterable {

    OnFilterChange mOnFilterChange = new OnFilterChange() {
        @Override
        public void onFilterChange(int count) {

        }
    };
    private List<ItemListaValor> items;
    private Context context;
    private AutoCompleteFilter mFilter = new AutoCompleteFilter();
    private List<ItemListaValor> valores = new LinkedList<ItemListaValor>();

    public QuestaoAutoCompleteAdapter(Context context, List<ItemListaValor> valores) {
        super(context, R.layout.simple_item_autocomplete);
        this.context = context;
        //SORTING
        Collections.sort(valores, new Comparator<ItemListaValor>() {
            @Override
            public int compare(ItemListaValor itemListaValor, ItemListaValor t1) {
                return itemListaValor.getDescricao().compareTo(t1.getDescricao());
            }
        });
        this.valores = valores;
    }

    public ItemListaValor getItemListaValor(int i){
        return valores.get(i);
    }

    public ItemListaValor getItemListaValor(String arvore){
        for(int i=0; i < valores.size(); i++){
            ItemListaValor item = valores.get(i);
            if(item.getDescricao().equalsIgnoreCase(arvore)){
                return valores.get(i);
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = LayoutInflater.from(context).inflate(R.layout.simple_item_autocomplete, null);
//        Typeface myTypefaceb = Typeface.createFromAsset(context.getAssets(), "fonts/segoeb.ttf");

        ImageView check = (ImageView) v.findViewById(R.id.check);
        check.setVisibility(View.GONE);

        TextView text1 = ((TextView) v.findViewById(R.id.text1));
//        text1.setTypeface(myTypefaceb);
        ItemListaValor ilv = items.get(i);
        if (ilv != null) {
            text1.setText(ilv.getDescricao());
        }
        return v;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public void setOnFilterChange(OnFilterChange mOnFilterChange) {
        this.mOnFilterChange = mOnFilterChange;
    }


    public interface OnFilterChange {
        void onFilterChange(int count);
    }

    public class AutoCompleteFilter extends Filter {


        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            if (charSequence != null) {
//                    synchronized (this) {
                List<ItemListaValor> i = new LinkedList<ItemListaValor>();

                for (ItemListaValor ilv : valores) {
                    if (ilv != null) {
                        if (ilv.getDescricao().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            i.add(ilv);
                        }
                    }
                }
                results.values = i;
                results.count = i.size();
//                    }
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, final FilterResults filterResults) {
                    notifyDataSetChanged();
                    if (filterResults.count > 0) {
                        items = (List<ItemListaValor>) filterResults.values;
                        notifyDataSetChanged();
                    } else {
                        items = new LinkedList<ItemListaValor>();
                        notifyDataSetInvalidated();
                    }
//                mOnFilterChange.onFilterChange(filterResults.count);
        }

    }

}
