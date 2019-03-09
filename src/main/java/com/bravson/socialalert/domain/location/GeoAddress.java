package com.bravson.socialalert.domain.location;

import org.springframework.data.geo.Point;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Setter(AccessLevel.NONE)
public class GeoAddress {

	private Point coordinates;
	private String formattedAddress;
	private String locality;
	private String country;
	
	public Double getLatitude() {
		return coordinates != null ? coordinates.getX() : null;
	}
	
	public Double getLongitude() {
		return coordinates != null ? coordinates.getY() : null;
	}
}
