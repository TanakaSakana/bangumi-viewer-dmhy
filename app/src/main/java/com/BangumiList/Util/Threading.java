package com.BangumiList.Util;

import com.BangumiList.bangumi.Bangumi;
import com.dmhyparser.utilparser.CombinedParser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Threading implements Callable<String> {
    CombinedParser combinedParser;
    Bangumi bangumi;

    public Threading(CombinedParser combinedParser, Bangumi bangumi) {
        this.combinedParser = combinedParser;
        this.bangumi = bangumi;
    }

    @Override
    public String call() throws Exception {

        // Search by Keyword
        ExecutorService service = Executors.newCachedThreadPool();
        List<Future> results = new ArrayList<>();
        // Open Threading for each keyword
        results.add(service.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return combinedParser.parse(bangumi.getName());
            }
        }));
        if (bangumi.getKeyword().size() >= 1) {
            for (final String s : bangumi.getKeyword()) {
                results.add(service.submit(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return combinedParser.parse(s);
                    }
                }));
            }
        }
        service.shutdown();
        // Evaluate the Keyword result
        for (Future<String> result : results) {
            String resultString = result.get();
            if (resultString != null)
                return resultString;
        }
        return null;
    }
}
