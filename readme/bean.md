##### WLocation

| Method Name | Description | Remark |
| :--| :-- | --- |
| getPower| Battery Percentage ||
| getFixStatus| Get Fixed Status |0 = Unable to locate; 1 = Single; 5 = Float; 4 = Fixed|
|getLatitude| Latitude |Unit: Degree|
| getLongitude|Longitude  |Unit: Degree|
| getAltitude|Altitude |Unit: Meter|
| getAltitudeCorr|Height Anomaly |Normal Height = Geodetic Height - Height anomaly|
| getnSatsInView| Satellite Number in View ||
| getnSatsInUse| Satellite Number in Use ||
| getTime| Get Timestamp ||
| getSpeed| Get Speed |Unit: Meter per Second|
| getBearing| Get Bearing ||
| getAccuracy| Get Horizontal Accuracy |Unit: Meter|
| getmVerticalAccuracy| Get Vertical Accuracy ||
| gethDOP| hdop||
| getvDOP| vdop||
| getpDOP| pdop||

##### SatelliteInfo
| Method Name | Description | Remark |
| :--| :-- | :-- |
| getType| Satellite Type ||
| getNSigs| Signal Number ||
| isUsed| Is in use ||
| getCn0| Signal-to-noise ratio ||
| getElev| Elevation ||
| getAzi| Azimuth ||
| getPrn| PRN ||
