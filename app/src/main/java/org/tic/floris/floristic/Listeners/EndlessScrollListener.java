package org.tic.floris.floristic.Listeners;

import android.widget.AbsListView;

public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {

    // the minimum amount of items to have below your current scroll position before loading more
    private int visibleThreshold = 5;
    // the current offset index of data you have loaded
    private int currentPage = 0;
    // the total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    // true if we are still waiting for the last set of data to load
    private boolean loading = true;
    // sets the starting page index
    private int startingPageIndex = 0;

    public EndlessScrollListener(int visibleThreshold, int startPage) {
        this.visibleThreshold = visibleThreshold;
        this.startingPageIndex = startPage;
        this.currentPage = startPage;
    }

    /**
     * this happens many times a second during a scroll, so be wary of the code you place here
     * we are given a few useful parameters to help us work out if we need to load some more data,
     * but first we check if we are waiting for the previous load to finish
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // remove the footerview from counts
        totalItemCount = totalItemCount - 1;
        visibleItemCount = visibleItemCount - 1;
        // if the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }
        // if it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;

        }

        // if it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data
        // if we do need to reload some more data, we execute onLoadMore to fetch the data
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            currentPage++;
            onLoadMore(currentPage, totalItemCount);
            loading = true;
        }
    }

    /**
     * defines the process for actually loading more data based on page
     */
    public abstract void onLoadMore(int page, int totalItemsCount);

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // don't take any action on changed
    }
}
