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
	}
}
