﻿-- Function: public.query11()

-- DROP FUNCTION public.query11();

CREATE OR REPLACE FUNCTION public.query11()
  RETURNS void AS
$BODY$DECLARE
	rec_row RECORD;
	rec_cursor CURSOR 
	FOR
	(select L1.OID as oid1,L2.OID as oid2, L3.OID as oid3
from "lod8" L1, "lod8" L2, "lod8" L3
where L1.sub = '<http://knoesis.wright.edu/ssw/System_NCSW1>'
and L1.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#processLocation>'
and L2.sub = L1.obj
and L2.pred = '<http://www.w3.org/2003/01/geo/wgs84_pos#alt>'
and L3.sub = L1.obj
and L3.pred = '<http://www.w3.org/2003/01/geo/wgs84_pos#lat>');

BEGIN
	OPEN rec_cursor;
	LOOP
		FETCH rec_cursor INTO rec_row;
		EXIT WHEN NOT FOUND;
		INSERT INTO LogTable(OID,start_time) values(rec_row.oid1,clock_timestamp());
		INSERT INTO LogTable(OID,start_time) values(rec_row.oid2,clock_timestamp());
		INSERT INTO LogTable(OID,start_time) values(rec_row.oid3,clock_timestamp());
	END LOOP;
CLOSE rec_cursor;
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.query11()
  OWNER TO postgres;
