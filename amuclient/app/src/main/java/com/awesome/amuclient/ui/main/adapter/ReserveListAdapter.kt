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
    }

    fun clearReserveLists() {
        if(this.reserveLists.isNotEmpty()) {
            this.reserveLists.clear()
            notifyDataSetChanged()
        }
    }

    fun getLastReserveId(position: Int) : String {
        return this.reserveLists[position].reserve.id.toString()
    }

    fun update(reserveLists: ArrayList<ReserveList>) {
        val endPosition = this.reserveLists.size
        loadMore(reserveLists, endPosition)
    }

    private fun loadMore(reserveLists: ArrayList<ReserveList>, endPosition: Int) {
        this.reserveLists.addAll(reserveLists)
        notifyItemRangeInserted(endPosition, this.reserveLists.size - endPosition)
    }
}
