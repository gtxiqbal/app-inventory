package id.co.gtx.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapperUtil {

    public static <T> T mappingClass(Object o, Class<T> tClass){
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(o, tClass);
    }

    public static <T> List<T> mappingClass(List listO, Class<T> tClass){
        ObjectMapper mapper = new ObjectMapper();
        return (List) listO.stream()
                .map(entity -> mapper.convertValue(entity, tClass))
                .collect(Collectors.toList());
    }

    public static <T> T mappingClass(ResultSet rs, Class<T> tClass) throws SQLException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = new HashMap<>();
        int rowCount = rs.getMetaData().getColumnCount();
        for (int i = 1; i <= rowCount; i++) {
            map.put(rs.getMetaData().getColumnLabel(i), rs.getObject(i));
        }
        return mapper.convertValue(map, tClass);
    }

}
