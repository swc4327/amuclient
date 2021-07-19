package com.awesome.amuclient.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.awesome.amuclient.R
import com.awesome.amuclient.data.model.ReserveList
import com.awesome.amuclient.data.model.Store

import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.item_reserve.view.*

class ReserveListAdapter(private val reserveLists : ArrayList<ReserveList>,
                         private val requestManager : RequestManager,
                         private val itemClick: (Store) -> Unit,
                         private val showDetailClick: (ReserveList)->Unit,
                         private val goReviewClick: (ReserveList)->Unit)
    : RecyclerView.Adapter<ReserveListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReserveListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reserve, parent, false)
        return ReserveListViewHolder(view, itemClick, showDetailClick, goReviewClick)
    }

    override fun getItemCount(): Int {
        return reserveLists.size
    }

    override fun onBindViewHolder(holder: ReserveListViewHolder, position: Int) {
        holder.bind(reserveLists[position], requestManager)

//        holder.itemView.go_review.setOnClickListener {
//
//        }
//
//        holder.itemView.show_detail.setOnClickListener {
//
//        }
    }

    fun update(reserveLists: ArrayList<ReserveList>) {
        if(this.reserveLists.isNotEmpty())
            this.reserveLists.clear()
        this.reserveLists.addAll(reserveLists)
        notifyDataSetChanged()
    }
}
