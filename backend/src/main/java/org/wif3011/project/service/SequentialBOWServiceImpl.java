package org.wif3011.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.wif3011.project.utility.ProcessFileServiceUtil;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SequentialBOWServiceImpl implements SequentialBOWService {

    private final ProcessFileServiceUtil processFileUtil;

    @Override
    public Map<String, Integer> sequentialWordMap(MultipartFile file) {
        String document = processFileUtil.convertFileToString(file);
        return processFileUtil.sequentialGenerateWordMap(document);
    }
}