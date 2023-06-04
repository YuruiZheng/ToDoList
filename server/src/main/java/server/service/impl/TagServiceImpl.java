package server.service.impl;

import commons.Tag;
import org.springframework.stereotype.Service;
import server.database.TagRepository;
import server.service.TagService;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    private static final String NO_SUCH_TAG = "Tag not found";
    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Tag createTag(Tag tag) {
        return tagRepository.save(tag);
    }

    @Override
    public List<Tag> getAll() {
        return tagRepository.findAll();
    }

    @Override
    public List<Tag> getByBoard(Integer id) {
        return tagRepository.findAllByBoardId(id);
    }

    @Override
    public Tag updateTag(Tag tag) throws IllegalArgumentException {
        Tag savedTag = tagRepository.findById(tag.getId())
                .orElseThrow(() -> new IllegalArgumentException(NO_SUCH_TAG));
        savedTag.applyUpdates(tag);
        tagRepository.save(savedTag);
        return savedTag;
    }

    @Override
    public void deleteById(Integer id) throws IllegalArgumentException {
        tagRepository.deleteById(id);

    }
}
