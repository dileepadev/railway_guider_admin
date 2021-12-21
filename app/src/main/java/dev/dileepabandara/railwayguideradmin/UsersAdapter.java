/*
   --------------------------------------
      Developed by
      Dileepa Bandara
      https://dileepabandara.github.io
      contact.dileepabandara@gmail.com
      ©dileepabandara.dev
      2020
   --------------------------------------
*/

package dev.dileepabandara.railwayguideradmin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import dev.dileepabandara.railwayguideradmin.R;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {

    Context context;
    ArrayList<UsersFromDB> bookedTrainsHelperClasses;

    public UsersAdapter(Context c, ArrayList<UsersFromDB> p) {
        context = c;
        bookedTrainsHelperClasses = p;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_booked_trains, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        final String getTicketID = bookedTrainsHelperClasses.get(position).getTicketID();
        final String getMobile = bookedTrainsHelperClasses.get(position).getMobile();
        final String getName = bookedTrainsHelperClasses.get(position).getName();

        holder.userMobile.setText(getMobile);
        holder.userTicket.setText(getTicketID);
        holder.userName.setText(getName);

        //Book selected train
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Good", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookedTrainsHelperClasses.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView userMobile, userTicket, userName;
        RelativeLayout relativeLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            userMobile = (TextView) itemView.findViewById(R.id.userMobile);
            userTicket = (TextView) itemView.findViewById(R.id.userTicket);
            userName = (TextView) itemView.findViewById(R.id.userName);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.linearLayout22);

        }
    }
}
