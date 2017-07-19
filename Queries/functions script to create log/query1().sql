-- Function: public.query1()

-- DROP FUNCTION public.query1();

CREATE OR REPLACE FUNCTION public.query1()
  RETURNS void AS
$BODY$DECLARE
	rec_row RECORD;
	col_row TEXT;
	rec_cursor CURSOR 
	FOR
	(select L1.OID as oid1,L2.OID as oid2 ,L3.OID as oid3,L4.OID as oid4,L5.OID as oid5 from "lod8" L1, "lod8" L2, "lod8" L3, "lod8" L4, "lod8" L5
	where
	L1.sub = '<http://knoesis.wright.edu/ssw/System_NWSN2>'
	and L1.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#generatedObservation>'
	and L2.sub = L1.obj
	and L2.pred = '<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>'
	and L2.obj = '<http://knoesis.wright.edu/ssw/ont/weather.owl#TemperatureObservation>'
	and L3.sub = L2.sub
	and L3.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#result>'
	and L4.obj = '<http://knoesis.wright.edu/ssw/ont/weather.owl#fahrenheit>'
	and L4.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#uom>'
	and L4.sub = L3.obj
	and L5.sub = L4.sub
	and L5.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#floatValue>');

BEGIN
	OPEN rec_cursor;
	LOOP
		FETCH rec_cursor INTO rec_row;
		EXIT WHEN NOT FOUND;
		LOOP
			FETCH rec_cursor INTO rec_row;
		EXIT WHEN NOT FOUND;
		INSERT INTO logtable(OID,start_time) values(rec_row.oid1,clock_timestamp());
		INSERT INTO logtable(OID,start_time) values(rec_row.oid2,clock_timestamp());
		INSERT INTO logtable(OID,start_time) values(rec_row.oid3,clock_timestamp());
		INSERT INTO logtable(OID,start_time) values(rec_row.oid4,clock_timestamp());
		INSERT INTO logtable(OID,start_time) values(rec_row.oid5,clock_timestamp());
		END LOOP;
	END LOOP;
CLOSE rec_cursor;
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.query1()
  OWNER TO postgres;
