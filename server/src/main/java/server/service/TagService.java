package server.service;

import commons.Tag;

import java.util.List;

public interface TagService {
    Tag createTag(Tag tag);
    List<Tag> getAll();
    List<Tag> getByBoard(Integer id);
    Tag updateTag(Tag tag);
    void deleteById(Integer id);
}
