package com.example.carspotter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.carspotter.model.Event;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class EventsFragment extends Fragment implements RecyclerViewInterface {

    private static final String QUEUE_URL = "https://studev.groept.be/api/a22pt304/GetEvents";

    private List<Event> allEvents = new ArrayList<>();
    private List<Event> events = new ArrayList<>();
    private List<Event> searchEvents = new ArrayList<>();
    private RecyclerView eventView;
    private RecyclerView searchView;
    private TextView noEventsTxt;
    private MaterialButtonToggleGroup toggleButton;
    private LinearProgressIndicator progressIndicatorCarView;
    private MaterialButton week;
    private MaterialButton month;
    private MaterialButton year;
    private ExtendedFloatingActionButton add_event_fab;

    private SearchBar search_bar;
    private SearchView search_view;
    private TextView searchTxt;


    View view;

    public EventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_events, container, false);

        eventView = (RecyclerView) view.findViewById(R.id.eventView);
        EventsAdapter adapter = new EventsAdapter(events, this);
        eventView.setAdapter(adapter);
        eventView.setLayoutManager(new LinearLayoutManager(getActivity()));

        noEventsTxt = (TextView) view.findViewById(R.id.noEventsTxt);

        toggleButton = (MaterialButtonToggleGroup) view.findViewById(R.id.toggleButton);
        progressIndicatorCarView = (LinearProgressIndicator) view.findViewById(R.id.progressIndicatorCarView);

        week = (MaterialButton) view.findViewById(R.id.week);
        month = (MaterialButton) view.findViewById(R.id.month);
        year = (MaterialButton) view.findViewById(R.id.year);

        progressIndicatorCarView.setVisibility(View.VISIBLE);

        add_event_fab = (ExtendedFloatingActionButton) view.findViewById(R.id.add_event_fab);

        searchView = (RecyclerView) view.findViewById(R.id.searchView);
        EventsAdapter adapter2 = new EventsAdapter(searchEvents, this);
        searchView.setAdapter(adapter2);
        searchView.setLayoutManager(new LinearLayoutManager(getActivity()));

        search_bar = (SearchBar) view.findViewById(R.id.search_bar);
        search_view = (SearchView) view.findViewById(R.id.search_view);
        searchTxt = (TextView) view.findViewById(R.id.searchTxt);

        if (searchEvents.size() == 0) {
            String text = "Press on the Searchbar above! ";
            text += new String(Character.toChars(0x1F446));
            searchTxt.setText(text);
        }

        requestEvents();

        toggleButton.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (checkedId == R.id.week && isChecked) {
                    updateUmoeder("week");
                } else if (checkedId == R.id.month && isChecked) {
                    updateUmoeder("month");
                } else if (checkedId == R.id.year && isChecked) {
                    updateUmoeder("year");
                } else if (!week.isChecked() && !month.isChecked() && !year.isChecked()) {
                    updateUmoeder("all");
                }
            }
        });


        eventView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                //Make action button invisible when scrolling
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 10 & add_event_fab.isShown()){
                    add_event_fab.hide();
                }
                if (dy < -10 && !add_event_fab.isShown()) {
                    add_event_fab.show();
                }
                if (!recyclerView.canScrollVertically(-1)) {
                    add_event_fab.show();
                }
                else if (!recyclerView.canScrollVertically(1)) {
                    add_event_fab.hide();
                }
            }
        });


        search_view.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                searchEvents.clear();
                List<Event> search= allEvents.stream()
                        .filter(e -> e.getName().toLowerCase().contains(search_view.getText().toString().toLowerCase()))
                        .collect(Collectors.toList());
                searchEvents.addAll(search);
                searchView.getAdapter().notifyDataSetChanged();

                if (searchEvents.size() != 0){
                    searchTxt.setText("");
                } else {
                    String text = "Event not found! ";
                    text += new String(Character.toChars(0x1F613));
                    searchTxt.setText(text);
                }

                return false;
            }
        });

        return view;
    }

    private void updateUmoeder(String sort){
        if (allEvents.size() != 0) {
            noEventsTxt.setText("");
            events.clear();
            for (Event event : allEvents) {
                Date date;
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(event.getDate());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                int days = (Period.between(LocalDate.now(), date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()).getDays());
                int months = (Period.between(LocalDate.now(), date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()).getMonths());
                int years = (Period.between(LocalDate.now(), date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()).getYears());

                if (sort.equals("week")) {
                    if (days <= 7 && days >= 0 && months == 0 && years == 0) {
                        events.add(event);
                    }
                } else if (sort.equals("month")) {
                    if (years == 0 && months == 0 && days >= 0) {
                        events.add(event);
                    }
                } else if (sort.equals("year")) {
                    if ((years == 0 && months > 0) || (years == 0 && months == 0 && days >= 0)) {
                        events.add(event);
                    }
                } else if (sort.equals("all")) {
                    if (years > 0 || (years == 0 && months > 0) || (years == 0 && months == 0 && days >= 0)) {
                        events.add(event);
                    }
                }


            }

        } else {
            String text = "No events planned at this moment! ";
            text += new String(Character.toChars(0x1F613));
            noEventsTxt.setText(text);
        }

        if (events.size() == 0){
            String text = "No events planned at this moment! ";
            text += new String(Character.toChars(0x1F613));
            noEventsTxt.setText(text);
        }
        else {
            String text = "";
            noEventsTxt.setText(text);
        }
        eventView.getAdapter().notifyDataSetChanged();
        //circularProgressIndicatorCarView.setVisibility(View.INVISIBLE);
    }


    private void requestEvents() {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                QUEUE_URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        processJSONResponse(response);
                        if (!week.isChecked() && !month.isChecked() && !year.isChecked()) {
                            updateUmoeder("all");
                        } else if (week.isChecked()) {
                            updateUmoeder("week");
                        } else if (month.isChecked()) {
                            updateUmoeder("month");
                        } else{
                            updateUmoeder("year");
                        }
//                        progressIndicatorCarView.setVisibility(View.INVISIBLE);
//                        eventView.getAdapter().notifyDataSetChanged();
                        progressIndicatorCarView.animate().alpha(0f).setDuration(500).setListener(null);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressIndicatorCarView.animate().alpha(0f).setDuration(500).setListener(null);
                        Toast.makeText(
                                getActivity(),
                                "Unable to communicate with the server",
                                Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(queueRequest);
    }

    private void processJSONResponse(JSONArray response) {
        allEvents.clear();
        for (int i = 0; i < response.length(); i++) {
            try {
                Event event = new Event(response.getJSONObject(i));
                allEvents.add(event);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(allEvents, (o1, o2) -> o1.getDate().compareTo(o2.getDate()));
    }

    @Override
    public void onItemClick(int position, String type) {

    }
}