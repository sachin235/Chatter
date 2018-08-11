package com.codingblocks.chatter.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codingblocks.chatter.R;
import com.codingblocks.chatter.db.MessagesTable;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {

    private List<MessagesTable> messages;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public TextView time;
        public TextView message;

        public ImageView userImage;

        public MyViewHolder(View view) {
            super(view);
            username = view.findViewById(R.id.username);
            time = view.findViewById(R.id.time);
            message = view.findViewById(R.id.message);
            userImage = view.findViewById(R.id.useravatar);
        }
    }

    public MessagesAdapter(List<MessagesTable> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @Override
    public MessagesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View itemView =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_item, parent, false);
        return new MessagesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MessagesAdapter.MyViewHolder myViewHolder, int i) {
        MessagesTable message = messages.get(i);
        myViewHolder.username.setText(message.getDisplayName());
        // A timestamp looks like this 2014-03-25T11:51:32.289Z
        String timestamp = message.getTimestamp();
        if (!timestamp.equals("sending")) {
            //= 11:51 2014-03-25
            try {

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                Date date = dateFormat.parse("2017-04-26T20:55:00.000Z");//You will get date object relative to server/client timezone wherever it is parsed
                DateFormat formatter = new SimpleDateFormat("MMM dd yyyy  HH:mm"); //If you need time just put specific format for time like 'HH:mm:ss'
                String dateStr = formatter.format(date);
                myViewHolder.time.setText(dateStr); // or sending

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Linkify.addLinks(myViewHolder.message, Linkify.WEB_URLS);
        Picasso.get().load(message.getUserAvater()).into(myViewHolder.userImage);
        myViewHolder.message.setText(message.getText());

        if (message.getMentionsIds().size() > 0) {
            SpannableString spannableString = new SpannableString(message.getText());
            for (int j = 0; j < message.getMentionsIds().size(); j++) {
                setClickableString(message.getMentionsIds().get(j).getScreenName(), message.getText(), message.getMentionsIds().get(j).getUserId(), myViewHolder.message, spannableString);
            }
        } else {
            myViewHolder.message.setText(message.getText());
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private void setClickableString(final String clickableValue, String wholeValue, final String userid, TextView yourTextView, SpannableString spannableString) {
        String value = wholeValue;
        int startIndex = value.indexOf(clickableValue);
        int endIndex = startIndex + clickableValue.length();
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false); // <-- this will remove automatic underline in set span
            }

            @Override
            public void onClick(View widget) {
                // do what you want with clickable value
                Bundle bundle = new Bundle();
                bundle.putString("userId", userid);
                bundle.putString("userName", clickableValue);
                Intent userIntent = new Intent(widget.getContext(), UserActivity.class);
                userIntent.putExtras(bundle);
                widget.getContext().startActivity(userIntent);
            }
        }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        yourTextView.setText(spannableString);
        yourTextView.setMovementMethod(LinkMovementMethod.getInstance()); // <-- important, onClick in ClickableSpan won't work without this
    }
}
