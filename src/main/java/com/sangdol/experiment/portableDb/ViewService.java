package com.sangdol.experiment.portableDb;

import java.util.List;

/**
 * @author hugh
 */
public class ViewService {
    private final ViewDao viewDao;

    public ViewService(ViewDao viewDao) {
        this.viewDao = viewDao;
    }

    public List<View> getLatest10Visitors(int userId) {
        // TODO exclude view which is older than 10 days
        return viewDao.getLatest10Visitors(userId);
    }
}
