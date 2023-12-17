package top.meethigher.count.page.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import top.meethigher.count.page.entity.IPv6;
import top.meethigher.count.page.repository.IPv6Repository;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

public class IPv6Utils {

    public static class IPv6 implements Serializable {

        private Integer code;

        private Map<String, Object> data;

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public Map<String, Object> getData() {
            return data;
        }

        public void setData(Map<String, Object> data) {
            this.data = data;
        }
    }


    private static final String api = "https://ip.zxinc.org/api.php?type=json&ip=%s";


    public static String queryIPv6LocFromAPI(String ipv6) {
        String api = String.format(IPv6Utils.api, ipv6);
        HttpResponse resp = HttpRequest.get(api)
                .headersClear()
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                .charset(StandardCharsets.UTF_8.name())
                .send();
        String s = resp.bodyText();
        ObjectMapper om = new ObjectMapper();
        try {
            IPv6 iPv6 = om.readValue(s, IPv6.class);
            if (iPv6.getCode() == 0) {
                return ((String) iPv6.getData().get("country")).replace("\t", "");
            }
            return null;
        } catch (Exception ignore) {
            return null;
        }
    }

    public static String queryIPv6LocFromAPI(IPv6Repository repository, String ipv6) {
        Optional<top.meethigher.count.page.entity.IPv6> optional = repository.findById(ipv6);
        if (optional.isPresent()) {
            return optional.get().getLoc();
        } else {
            String s = queryIPv6LocFromAPI(ipv6);
            if (s != null) {
                top.meethigher.count.page.entity.IPv6 entity = new top.meethigher.count.page.entity.IPv6(ipv6, s);
                repository.save(entity);
            }
            return s;
        }
    }

    public static void main(String[] args) throws Exception {
        String x = queryIPv6LocFromAPI("240e:878:43:35a6:7009:d86b:a4a6:76d");
        System.out.println(x);
    }
}
