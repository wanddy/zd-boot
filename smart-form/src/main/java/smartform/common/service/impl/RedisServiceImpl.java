package smartform.common.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import smartform.common.service.RedisService;
import smartform.common.model.BaseData;
import smartform.common.model.PageList;
import smartform.common.redis.JedisBaseView;
import smartform.common.redis.JedisMgr;
import smartform.common.redis.JedisViewMgr;

import java.io.IOException;
import java.util.Date;
import java.util.List;


public class RedisServiceImpl<T extends BaseData> implements RedisService<T> {

    @Override
    public String getKey(String id) {
        return id;
    }

    @Override
    public Class<T> getClazz() {
        return null;
    }

    @Override
    public T get(String id) {
        try {
            return JedisMgr.GetInstance().get(getKey(id), getClazz());
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchFieldException
                | SecurityException | IllegalArgumentException | IOException e) {

            e.printStackTrace();
        }
        return null;
    }

    @Override
    public T get(String id, String... params) {
        try {
            return JedisMgr.GetInstance().get(getKey(id), getClazz(), params);
        } catch (InstantiationException | IllegalAccessException | NoSuchFieldException | SecurityException
                | ClassNotFoundException | IllegalArgumentException | IOException e) {

            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(BaseData entity) {
        try {
            String key = getKey(entity.getId());
            JedisMgr.GetInstance().update(key, entity);
            if (hasView()) {
                JedisViewMgr.SaveView(getViewKey(), key, new Date().getTime());
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException
                | JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(BaseData entity, String... params) {
        try {
            String key = getKey(entity.getId());
            JedisMgr.GetInstance().update(key, entity, params);
            if (hasView()) {
                JedisViewMgr.SaveView(getViewKey(), key, new Date().getTime());
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException
                | JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateOnce(String id, String name, String value) {
        String key = getKey(id);
        JedisMgr.GetInstance().updateOnce(key, name, value);
        if (hasView()) {
            try {
                JedisViewMgr.SaveView(getViewKey(), key, new Date().getTime());
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void delete(String id) {
        String key = getKey(id);
        JedisMgr.GetInstance().delete(key);
        if (hasView()) {
            JedisViewMgr.RemoveView(getViewKey(), key);
        }
    }

    @Override
    public void delete(String id, String... params) {
        String key = getKey(id);
        JedisMgr.GetInstance().deleteParam(key, params);
        if (hasView()) {
            try {
                JedisViewMgr.SaveView(getViewKey(), key, new Date().getTime());
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean hasKey(String id) {
        return JedisMgr.GetInstance().hasKey(getKey(id));
    }

    @Override
    public boolean hasView() {
        return false;
    }

    @Override
    public String getViewKey() {
        return getClazz().getSimpleName() + "View";
    }

    @Override
    public PageList<T> getList(int pageNum, int pageSize) {
        PageList<T> page = new PageList<T>(pageSize, pageNum);
        if (!hasView())
            return page;
        page.setTotal(JedisMgr.GetInstance().getSortSetLength(getViewKey()));
        int offset = pageNum * pageSize;
        int count = pageSize;
        List<JedisBaseView> viewlist;
        try {
            viewlist = JedisViewMgr.LoadViewRev(getViewKey(), offset, count, JedisBaseView.class);
            for (JedisBaseView view : viewlist) {
                page.getRows().add(JedisMgr.GetInstance().get(view.value, getClazz()));
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return page;
    }

    @Override
    public PageList<T> getList(int pageNum, int pageSize, String... params) {
        PageList<T> page = new PageList<T>(pageSize, pageNum);
        if (!hasView()) {
            return page;
        }
        page.setTotal(JedisMgr.GetInstance().getSortSetLength(getViewKey()));
        int offset = pageNum * pageSize;
        int count = pageSize;
        List<JedisBaseView> viewlist;
        try {
            viewlist = JedisViewMgr.LoadViewRev(getViewKey(), offset, count, JedisBaseView.class);
            for (JedisBaseView view : viewlist) {
                page.getRows().add(JedisMgr.GetInstance().get(view.value, getClazz(), params));
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchFieldException | SecurityException | ClassNotFoundException | IllegalArgumentException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return page;
    }
}
