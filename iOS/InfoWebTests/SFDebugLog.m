/*******************************************************************************
 Copyright (c) 2013, Christos Sotiriou
 All rights reserved.
 
 Redistribution and use in source and binary forms, with or without modification, are permitted
 provided that the following conditions are met:
 
 -- Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 -- Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 
 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/

#import "SFDebugLog.h"



void _SFDebugLog(const char *file, int lineNumber, const char *funcName, NSString *format,...) {
	@autoreleasepool {
		va_list ap;
		
		va_start (ap, format);
		if (![format hasSuffix: @"\n"]) {
			format = [format stringByAppendingString: @"\n"];
		}
		NSString *body =  [[NSString alloc] initWithFormat: format arguments: ap];
		va_end (ap);
		const char *threadName = [[[NSThread currentThread] name] UTF8String];
		NSString *fileName=[[NSString stringWithUTF8String:file] lastPathComponent];
		//NSDateFormatter *dateFormatter = [[[NSDateFormatter alloc] init] autorelease];
		//dateFormatter.dateFormat = @"hh:mm:ss.SSS";
		//NSDate *currentDate = [NSDate date];
		//NSString *dateString = [dateFormatter stringFromDate:currentDate];
		if (threadName) {
			fprintf(stderr,"%s/%s (%s:%d) %s", threadName,funcName,[fileName UTF8String],lineNumber,[body UTF8String]);
		} else {
			fprintf(stderr,"%p/%s (%s:%d) %s", [NSThread currentThread],funcName,[fileName UTF8String],lineNumber,[body UTF8String]);
		}
	}
}

