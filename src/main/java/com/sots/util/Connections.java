package com.sots.util;

public enum Connections{
	NORTH{
		public String toString() {
			return "UP";
		}
	},
	SOUTH{
		public String toString() {
			return "DOWN";
		}
	},
	EAST{
		public String toString() {
			return "EAST";
		}
	},
	WEST{
		public String toString() {
			return "WEST";
		}
	},
	UP{
		public String toString() {
			return "SOUTH";
		}
	},
	DOWN{
		public String toString() {
			return "NORTH";
		}
	},
	C_NORTH{
		public String toString() {
			return "CUP";
		}
	},
	C_SOUTH{
		public String toString() {
			return "CDOWN";
		}
	},
	C_EAST{
		public String toString() {
			return "CEAST";
		}
	},
	C_WEST{
		public String toString() {
			return "CWEST";
		}
	},
	C_UP{
		public String toString() {
			return "CSOUTH";
		}
	},
	C_DOWN{
		public String toString() {
			return "CNORTH";
		}
	},
}
