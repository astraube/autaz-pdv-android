package com.autazcloud.pdv.executor.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.autazcloud.pdv.R;
import com.autazcloud.pdv.domain.constants.DateFormats;
import com.autazcloud.pdv.domain.enums.SaleStatusEnum;
import com.autazcloud.pdv.domain.interfaces.SaleControllerInterface;
import com.autazcloud.pdv.domain.models.SaleModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SalesGridAdapter extends BaseAdapter {
	
    private Context mContext;
    private List<SaleModel> mList;
    private SaleControllerInterface mSaleCtrl;

    public SalesGridAdapter(Context context, SaleControllerInterface saleCtrl) {
    	this.mContext = context;
        this.mSaleCtrl = saleCtrl;
    }

	public void setData(List<SaleModel> list) {
		//Log.v(getClass().getSimpleName(), "setData - " + list.toString());
		this.mList = list;
		this.notifyDataSetChanged();
	}
    
    @Override
    public int getCount() {
    	if (mList != null)
    		return mList.size();
    	else
    		return 0;
    }
    
    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
    	final SaleModel sale = mList.get(position);
    	ViewHolder holder;
    	
    	if (convertView == null) {
    		LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		convertView = inflater.inflate(R.layout.layout_sale_grid_item, null);
            
            holder = new ViewHolder(convertView);
			
			convertView.setClickable(true);
			convertView.setTag(holder);
    	} else {
    		holder = (ViewHolder) convertView.getTag();
    	}

		String date = DateFormats.formatDateCommercial(sale.getDateCreated());

		holder.txtDate.setText(date);
        holder.txtNameSaleView.setText(sale.getClientName());

		if (!sale.getCodeControll().isEmpty())
        	holder.txtCodeSaleView.setText(convertView.getResources().getString(R.string.txt_bt_sale_code_controll, sale.getCodeControll()));


		int pCount = sale.getCountProducts();
		if (pCount == 1)
        	holder.txtQtdItemsView.setText(convertView.getResources().getString(R.string.txt_bt_sale_num_item, String.valueOf(pCount)));
		else if (pCount > 1)
			holder.txtQtdItemsView.setText(convertView.getResources().getString(R.string.txt_bt_sale_num_items, String.valueOf(pCount)));


        if (sale.getStatus().equals(SaleStatusEnum.OPEN.toString())) {
            convertView.setBackground(ContextCompat.getDrawable(parent.getContext(), R.drawable.bg_button_sale_grid));

        } else if (sale.getStatus().equals(SaleStatusEnum.CANCELED.toString())) {

            convertView.setBackground(ContextCompat.getDrawable(parent.getContext(), R.drawable.bg_button_sale_grid_canceled));

        } else if (sale.getStatus().equals(SaleStatusEnum.PAID.toString())) {

            convertView.setBackground(ContextCompat.getDrawable(parent.getContext(), R.drawable.bg_button_sale_grid_paid));

		} else if (sale.getStatus().equals(SaleStatusEnum.PAID_PARTIAL.toString())) {

            convertView.setBackground(ContextCompat.getDrawable(parent.getContext(), R.drawable.bg_button_sale_grid_partial_paid));
		}

        /*
        // Para o caso do primeiro botao da lista ser o botao de "Nova Venda"

    	if (sale != null) {
    		holder.txtNameSaleView.setText(sale.getClientName());
    		holder.txtCodeSaleView.setText(convertView.getResources().getString(R.string.txt_bt_sale_code_controll, sale.getCodeControll()));
    		holder.txtQtdItemsView.setText(convertView.getResources().getString(R.string.txt_bt_sale_num_itens, String.valueOf(sale.getCountProducts())));
    		
    		if (sale.getStatus().equals(SaleStatusEnum.CANCELED.toString())) {
        		convertView.setBackground(parent.getResources().getDrawable(R.drawable.bg_button_sale_cancel));
        	} else if (sale.getStatus().equals(SaleStatusEnum.PAID.toString())) {
        		convertView.setBackground(parent.getResources().getDrawable(R.drawable.bg_button_sale_grid_paid));
        	} else if (sale.getStatus().equals(SaleStatusEnum.PAID_PARTIAL.toString())) {
        		convertView.setBackground(parent.getResources().getDrawable(R.drawable.bg_button_sale_grid_partial_paid));
        	}
    	} else {
    		// Botao Nova Venda
    		holder.txtNameSaleView.setText(R.string.action_sale_new);
    		convertView.setBackground(parent.getResources().getDrawable(R.drawable.bg_button_sale_grid_new_sale));
    	}*/
    	
    	convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                mSaleCtrl.onOpenSale(sale);
                /*
                // Para o caso do primeiro botao da lista ser o botao de "Nova Venda"

				if (sale != null) {
					mSaleCtrl.onOpenSale(sale);
				} else {
					mSaleCtrl.onAddNewSale(null, true);
				}
				*/
			}
	    });
        return convertView;
    }

	public static class ViewHolder {
		@BindView(R.id.txtNomeVenda) TextView txtNameSaleView;
		@BindView(R.id.txtCode) TextView txtCodeSaleView;
		@BindView(R.id.txtQtdItems) TextView txtQtdItemsView;
		@BindView(R.id.txtDate) TextView txtDate;

		public ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}
	}
}
