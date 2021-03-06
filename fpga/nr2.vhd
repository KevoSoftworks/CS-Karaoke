LIBRARY IEEE;
USE IEEE.std_logic_1164.ALL;
USE IEEE.numeric_std.ALL;


ENTITY nr2 IS
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
	display6 : OUT STD_LOGIC_VECTOR(6 DOWNTO 0));
END ENTITY nr2;


ARCHITECTURE bhv OF nr2 IS

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

BEGIN

PROCESS(reset, SCLK)
	VARIABLE i : INTEGER := 0;
	VARIABLE c : INTEGER := 0;
	VARIABLE code : STD_LOGIC_VECTOR(15 DOWNTO 0) := "0000000000000000";
	--used for reading data input from MOSI
	VARIABLE converted : STD_LOGIC_VECTOR(15 DOWNTO 0) := "0000000000000000";
	--because of msbits and lsbytes
	VARIABLE timeframe : STD_LOGIC_VECTOR(15 DOWNTO 0) := "0000000000000000";
	--information about the timeframe of the audio samples are stored in here
	VARIABLE nr_of_bytes : INTEGER range 4 to 8192;
	CONSTANT marge : INTEGER := 512;
	--level of trigger for zero crossings
	VARIABLE zerocrossings : INTEGER := 0;
	VARIABLE amplitudesign : STD_LOGIC;
	VARIABLE std_zero : STD_LOGIC_VECTOR(15 DOWNTO 0) := "0000000000000000";

	BEGIN
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
		ELSIF SCLK'event AND SCLK = '1' THEN --active edge of SPI clock
			code(15-i) := MOSI; --read input
			converted := code(7 DOWNTO 0) & code(15 DOWNTO 8);
			i := i + 1;
			IF i = 16 THEN i := 0; c := c + 2; END IF; --reset i, increment c
			IF c >= nr_of_bytes THEN c := 0; END IF; --reset counter c
			IF c = 0 THEN 
				MISO <= std_zero(15-i); --start sending nr. of zero crossings
			ELSIF c = 2 THEN --sending timeframe information
				display1 <= hex2display(timeframe(3 DOWNTO 0)); --also display zerocrossings for debugging
	 			display2 <= hex2display(timeframe(7 DOWNTO 4));
	 			display3 <= hex2display(timeframe(11 DOWNTO 8));
	 			display4 <= hex2display(timeframe(15 DOWNTO 12));
				MISO <= timeframe(15-i); 
				IF i = 0 THEN
					IF TO_INTEGER(UNSIGNED(converted(12 DOWNTO 0))) < 4 THEN nr_of_bytes := 4;
					ELSE nr_of_bytes := TO_INTEGER(UNSIGNED(converted(12 DOWNTO 0))) + 1; END IF;
					zerocrossings := 0; 
					std_zero := "0000000000000000";
				END IF;
			ELSIF c = 4 AND i = 0 THEN timeframe := converted; MISO <= '0'; --set output to 0 again
			ELSIF c = 6 AND i = 0 THEN --determine initial amplitude sign
				IF TO_INTEGER(SIGNED(converted)) > 0 THEN amplitudesign := '1';
				END IF;
			ELSIF c > 6 AND i = 0 THEN --determine if the current data has crossed zero
				IF TO_INTEGER(SIGNED(converted)) < -marge AND amplitudesign = '1' THEN
					zerocrossings := zerocrossings + 1;
					amplitudesign := '0';
					std_zero := STD_LOGIC_VECTOR(TO_UNSIGNED(zerocrossings, 16));
				ELSIF TO_INTEGER(SIGNED(converted)) > marge AND amplitudesign = '0' THEN
					zerocrossings := zerocrossings + 1;
					amplitudesign := '1';
					std_zero := STD_LOGIC_VECTOR(TO_UNSIGNED(zerocrossings, 16));
				END IF;
			END IF;
		END IF;
	END PROCESS;
END;