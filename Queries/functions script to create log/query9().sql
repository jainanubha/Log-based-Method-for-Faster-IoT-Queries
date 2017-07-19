﻿-- Function: public.query9()

-- DROP FUNCTION public.query9();

CREATE OR REPLACE FUNCTION public.query9()
  RETURNS void AS
$BODY$DECLARE
	rec_row RECORD;
	rec_cursor CURSOR 
	FOR
	(select L1.OID as oid1,L2.OID as oid2,L3.OID as oid3,L4.OID as oid4,L5.OID as oid5,L6.OID as oid6
from "lod8" L1, "lod8" L2, "lod8" L3, "lod8" L4, "lod8" L5, "lod8" L6
where
L1.obj = '<http://knoesis.wright.edu/ssw/point_NRF>'
and L1.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#processLocation>'
and L2.sub = L1.sub
and L2.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#generatedObservation>'
and L3.sub = L2.obj
and L3.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#observedProperty>'
and L3.obj = '<http://knoesis.wright.edu/ssw/ont/weather.owl#_DewPoint>'
and L4.sub = L3.sub
and L4.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#result>'
and L5.obj = '<http://knoesis.wright.edu/ssw/ont/weather.owl#fahrenheit>'
and L5.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#uom>'
and L5.sub = L4.obj
and L6.sub = L5.sub
and L6.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#floatValue>');

BEGIN
	OPEN rec_cursor;
	LOOP
		FETCH rec_cursor INTO rec_row;
		EXIT WHEN NOT FOUND;
		INSERT INTO LogTable(OID,start_time) values(rec_row.oid1,clock_timestamp());
		INSERT INTO LogTable(OID,start_time) values(rec_row.oid2,clock_timestamp());
		INSERT INTO LogTable(OID,start_time) values(rec_row.oid3,clock_timestamp());
		INSERT INTO LogTable(OID,start_time) values(rec_row.oid4,clock_timestamp());
		INSERT INTO LogTable(OID,start_time) values(rec_row.oid5,clock_timestamp());
		INSERT INTO LogTable(OID,start_time) values(rec_row.oid6,clock_timestamp());
	END LOOP;
CLOSE rec_cursor;
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.query9()
  OWNER TO postgres;
