package w.wexpense.rest.utils;

import w.wexpense.model.DBable;
import w.wexpense.rest.dto.DBableDTO;

public class DTOutils {

	public static DBableDTO toDto(DBable<?> source) {
		if (source == null)
			return null;
		DBableDTO dto = new DBableDTO(source.getId(), source.getVersion(), source.getUid(), source.toString());
		return dto;
	}

	public static <T extends DBable<T>> T fromDto(DBableDTO source, Class<T> clazz) {
		if (source == null)
			return null;
		try {
			T t = clazz.newInstance();
			t.setId(source.getId());
			t.setVersion(source.getVersion());
			t.setUid(source.getUid());
			return t;
		} catch (Exception e) {
			throw new RuntimeException("Failed to instanciate " + clazz, e);
		}
	}
}
