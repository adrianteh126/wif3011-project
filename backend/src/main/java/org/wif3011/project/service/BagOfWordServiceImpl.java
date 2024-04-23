package org.wif3011.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.wif3011.project.utility.ProcessFileServiceUtil;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class BagOfWordServiceImpl implements BagOfWordService{

    private final ProcessFileServiceUtil processFileUtil;
    private final ConcurrentService concurrentService;
    private final SecondConcurrentService secondConcurrentService;


    @Override
    public Map<String, Integer> concurrentWordCount1(MultipartFile file) {
        String document = processFileUtil.convertFileToString(file);
        document = processFileUtil.concurrentFilterDocs(document);

        return concurrentService.javaStreamMethod(document);
    }

    @Override
    public Map<String, Integer> concurrentWordCount2(MultipartFile file){
        String document = processFileUtil.convertFileToString(file);
        document = processFileUtil.concurrentFilterDocs(document);

        return secondConcurrentService.forkJoinMethod(document);
    }
}
