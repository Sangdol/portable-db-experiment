package com.sangdol.experiment.portableDb;

import java.util.List;

/**
 * TODO See if this layer needed
 * @author hugh
 */
public class ViewService {
    private final ViewDao viewDao;

    public ViewService(ViewDao viewDao) {
        this.viewDao = viewDao;
    }

    public List<View> getLatest10Visitors(int userId) {
        return viewDao.getLatest10Visitors(userId);
    }

    public int createView(int hostId, int visitorId) {
        return viewDao.createView(hostId, visitorId);
    }

    public void clear() {
        viewDao.clear();
    }

    public List<Integer> getAllViewCounts() {
        return viewDao.getAllViewCounts();
    }
}
