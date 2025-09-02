package br.com.minhaentrada.victor.challenge.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.minhaentrada.victor.challenge.data.event.Event
import br.com.minhaentrada.victor.challenge.databinding.EventListItemBinding
import java.text.SimpleDateFormat
import java.util.Locale

class EventAdapter(
    private val onItemClicked: (Event) -> Unit
) : ListAdapter<Event, EventAdapter.EventViewHolder>(EventDiffCallback()) {

    inner class EventViewHolder(private val binding: EventListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            binding.eventTitleTextView.text = event.title
            binding.eventLocationTextView.text = "${event.city} - ${event.state}"
            binding.eventDayTextView.text = dayFormat.format(event.eventDate)
            binding.eventMonthTextView.text = monthFormat.format(event.eventDate).uppercase(Locale.getDefault())
            binding.eventCategoryChip.setCategory(event.category)
            itemView.setOnClickListener {
                onItemClicked(event)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = EventListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
    }

    class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private val dayFormat = SimpleDateFormat("dd", Locale.getDefault())
        private val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())
    }
}