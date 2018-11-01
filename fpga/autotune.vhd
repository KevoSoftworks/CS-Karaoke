LIBRARY IEEE;
USE IEEE.std_logic_1164.ALL;
USE IEEE.numeric_std.ALL;


ENTITY autotune IS
	PORT(
	reset : IN STD_LOGIC;
	SCLK : IN STD_LOGIC; 
	MOSI : IN STD_LOGIC; 
	MISO : OUT STD_LOGIC; 
	display1 : OUT STD_LOGIC_VECTOR(6 DOWNTO 0);
	display2 : OUT STD_LOGIC_VECTOR(6 DOWNTO 0);
	display3 : OUT STD_LOGIC_VECTOR(6 DOWNTO 0);
	display4 : OUT STD_LOGIC_VECTOR(6 DOWNTO 0);
	display5 : OUT STD_LOGIC_VECTOR(6 DOWNTO 0);
	display6 : OUT STD_LOGIC_VECTOR(6 DOWNTO 0);
	switch9 : IN STD_LOGIC;
	switch8 : IN STD_LOGIC);
END ENTITY autotune;


ARCHITECTURE bhv OF autotune IS

FUNCTION hex2display (n:std_logic_vector(3 DOWNTO 0)) RETURN std_logic_vector IS

VARIABLE res : std_logic_vector(6 DOWNTO 0);
  BEGIN
    CASE n IS          --        gfedcba; low active
	    WHEN "0000" => RETURN NOT "0111111";
	    WHEN "0001" => RETURN NOT "0000110";
	    WHEN "0010" => RETURN NOT "1011011";
	    WHEN "0011" => RETURN NOT "1001111";
	    WHEN "0100" => RETURN NOT "1100110";
	    WHEN "0101" => RETURN NOT "1101101";
	    WHEN "0110" => RETURN NOT "1111101";
	    WHEN "0111" => RETURN NOT "0000111";
	    WHEN "1000" => RETURN NOT "1111111";
	    WHEN "1001" => RETURN NOT "1101111";
	    WHEN "1010" => RETURN NOT "1110111";
	    WHEN "1011" => RETURN NOT "1111100";
	    WHEN "1100" => RETURN NOT "0111001";
	    WHEN "1101" => RETURN NOT "1011110";
	    WHEN "1110" => RETURN NOT "1111001";
	    WHEN OTHERS => RETURN NOT "1110001";			
    END CASE;
  END hex2display;

	
TYPE mem IS ARRAY(0 TO 4095) OF STD_LOGIC_VECTOR(15 DOWNTO 0); --create inferred RAM
SIGNAL ram_block : mem;

BEGIN

PROCESS(reset, SCLK, switch9, switch8)
	VARIABLE i : INTEGER := 0;
	VARIABLE c : INTEGER := 0;
	VARIABLE code : STD_LOGIC_VECTOR(15 DOWNTO 0) := "0000000000000000";
	--used for reading data input from MOSI
	VARIABLE converted : STD_LOGIC_VECTOR(15 DOWNTO 0) := "0000000000000000";
	--because of msbits and lsbytes
	VARIABLE timeframe : STD_LOGIC_VECTOR(15 DOWNTO 0) := "0000000000000000";
	--information about the timeframe of the audio samples are stored in here
	VARIABLE nr_of_bytes : INTEGER range 4 to 8192;
	CONSTANT marge : INTEGER := 256;
	--level of trigger for zero crossings
	VARIABLE zerocrossings : INTEGER := 0;
	VARIABLE amplitudesign : STD_LOGIC;
	VARIABLE std_zero : STD_LOGIC_VECTOR(15 DOWNTO 0) := "0000000000000000";
	VARIABLE zeros_original : STD_LOGIC_VECTOR(15 DOWNTO 0) := "0000000000000000";
	VARIABLE write_address : INTEGER := 0; --to access the inferred RAM
	VARIABLE read_address: INTEGER := 0;
	VARIABLE autotuned : STD_LOGIC_VECTOR(15 DOWNTO 0); --the samples from the previous packet will be read into this vector
	VARIABLE marge_switch : INTEGER range 256 to 2048;
	VARIABLE vectorswitch : STD_LOGIC_VECTOR(15 DOWNTO 0) := STD_LOGIC_VECTOR(TO_UNSIGNED(marge_switch, 16));
	VARIABLE write_high : STD_LOGIC := '0';
	VARIABLE WE : STD_LOGIC := '0';
	VARIABLE RE : STD_LOGIC := '0';

	BEGIN
		IF switch9 = '0' AND switch8 = '0' THEN marge_switch := marge;
		ELSIF switch9 = '0' AND switch8 = '1' THEN marge_switch := 2*marge;
		ELSIF switch9 = '1' AND switch8 = '0' THEN marge_switch := 4*marge;
		ELSE marge_switch := 8*marge; END IF;
			display5 <= hex2display(vectorswitch(11 DOWNTO 8));

		IF reset = '0' THEN
			i := 0;
			c := 0;
			MISO <= '0';
			code := "0000000000000000";
	 		display1 <= hex2display("0000");
	 		display2 <= hex2display("0000");
	 		display3 <= hex2display("0000");
	 		display4 <= hex2display("0000");
	 		display5 <= hex2display("0000");
	 		display6 <= hex2display("0000");
			write_address := 0;
			read_address := 0;
			autotuned := "0000000000000000";

		ELSIF SCLK'event AND SCLK = '1' THEN --active edge of SPI clock
			code(15-i) := MOSI; --read input
			converted := code(7 DOWNTO 0) & code(15 DOWNTO 8);
			i := i + 1;
			IF i = 16 THEN i := 0; c := c + 2; END IF; --reset i, increment c
			IF c >= nr_of_bytes THEN 
				c := 0; 
				RE := '0';
				WE := '0';
				IF write_high = '0' THEN 
					write_high := '1';
				ELSE write_high := '0'; END IF;
			END IF; --reset counter c
			IF c = 0 THEN 
				MISO <= std_zero(15-i); --start sending nr. of zero crossings
				display1 <= hex2display(std_zero(3 DOWNTO 0)); --also display zerocrossings for debugging
	 			display2 <= hex2display(std_zero(7 DOWNTO 4));
	 			display3 <= hex2display(std_zero(11 DOWNTO 8));
	 			display4 <= hex2display(std_zero(15 DOWNTO 12));
			ELSIF c = 2 THEN --sending timeframe information
				MISO <= timeframe(15-i); 
				IF i = 0 THEN
					IF TO_INTEGER(UNSIGNED(converted(11 DOWNTO 0))) < 4 THEN nr_of_bytes := 4;
					ELSE nr_of_bytes := TO_INTEGER(UNSIGNED(converted(11 DOWNTO 0))) + 1; END IF;
					zerocrossings := 0; 
					std_zero := "0000000000000000";
				END IF;
			ELSIF c = 4 THEN
				IF i = 0 THEN 
					timeframe := converted; 
					MISO <= '0'; --set output to 0 again
				ELSIF i = 15 THEN
					RE := '1';
					WE := '1';
				END IF;
			ELSIF c = 6 THEN 
				IF i = 0 THEN 
					zeros_original := converted; --read number of zero crossings into program
				END IF;
				MISO <= autotuned(15-i);
			ELSIF c = 8 THEN
				IF i = 0 THEN --determine initial amplitude sign
					IF TO_INTEGER(SIGNED(converted)) > 0 THEN amplitudesign := '1'; 
					ELSE amplitudesign := '0'; END IF;
				END IF;
				MISO <= autotuned(15-i);
			ELSIF c > 8 THEN
				IF i = 0 THEN --determine if the current data has crossed zero
					IF TO_INTEGER(SIGNED(converted)) < -marge_switch AND amplitudesign = '1' THEN
						zerocrossings := zerocrossings + 1;
						amplitudesign := '0';
						std_zero := STD_LOGIC_VECTOR(TO_UNSIGNED(zerocrossings, 16));
					ELSIF TO_INTEGER(SIGNED(converted)) > marge_switch AND amplitudesign = '0' THEN
						zerocrossings := zerocrossings + 1;
						amplitudesign := '1';
						std_zero := STD_LOGIC_VECTOR(TO_UNSIGNED(zerocrossings, 16));
					END IF;
				END IF;
				MISO <= autotuned(15-i);
			END IF;
			IF i = 0 AND RE = '1' THEN
				IF write_high = '0' THEN --either read from the top of the memory or the bottom
					autotuned := ram_block(2048 + read_address); --start sending previous packet samples
				ELSE autotuned := ram_block(read_address); END IF;
			END IF;
				

		ELSIF SCLK'event AND SCLK = '0' THEN --load samples into ram after the data is read so no data will be lost
			IF i = 0 AND WE = '1' THEN 
				IF write_high = '0' THEN ram_block(write_address) <= code; --write to the top or bottom half of the memory
				ELSE ram_block(2048 + write_address) <= code; END IF;
				write_address := write_address + 1;
				read_address := read_address + 1;
			END IF;
			IF write_address = nr_of_bytes/2 THEN write_address := 0; END IF;
			IF read_address = nr_of_bytes/2 THEN read_address := 0; END IF;
			IF c = 0 THEN write_address := 0; read_address := 0; END IF;
		END IF;
	END PROCESS;
END;