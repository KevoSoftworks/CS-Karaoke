LIBRARY IEEE;
USE IEEE.STD_LOGIC_1164.ALL;

ENTITY communication IS
	PORT(
	reset : IN STD_LOGIC;
	SCLK : IN STD_LOGIC; 
	MOSI : IN STD_LOGIC; 
	MISO : OUT STD_LOGIC; 
	display1 : OUT STD_LOGIC_VECTOR(6 DOWNTO 0);
	display2 : OUT STD_LOGIC_VECTOR (6 DOWNTO 0) );
END ENTITY communication;

ARCHITECTURE behavior OF communication IS

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
 VARIABLE code : STD_LOGIC_VECTOR(7 DOWNTO 0);
 VARIABLE code2: STD_LOGIC_VECTOR(7 DOWNTO 0);
 BEGIN
	IF reset = '0' THEN
	 i := 0;
         MISO <= '0';
	 display1 <= "0000000";
	 display2 <= "0000000";
	ELSIF SCLK'event AND SCLK = '1' THEN
	 code(7-i) := MOSI;
	 IF i < 7 THEN MISO <= code(6-i); END IF;
	 i := i + 1;
	 display1 <= hex2display(code(3 DOWNTO 0));
	 display2 <= hex2display(code(7 DOWNTO 4));
	 IF i = 8 THEN i := 0; MISO <= code(7-i); END IF;
	END IF;
 END PROCESS;
END;
