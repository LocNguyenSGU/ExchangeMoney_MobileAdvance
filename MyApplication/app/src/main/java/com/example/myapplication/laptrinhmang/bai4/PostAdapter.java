package com.example.myapplication.laptrinhmang.bai4;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;

    public PostAdapter(List<Post> posts) {
        this.postList = posts;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bai4_item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.textTitle.setText(post.getTitle());
        holder.textBody.setText(post.getBody());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textBody;

        PostViewHolder(View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textBody = itemView.findViewById(R.id.textBody);
        }
    }
}

