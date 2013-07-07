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
#import <Foundation/Foundation.h>

@interface NSDictionary (SFAdditions)

/**
 *	@brief	Normalizes the values of NSDictionary. NSDictionary cannot hold nil values, but
 * it can hold NSNull instances, where JSON returns "null". In those cases, if an object is assigned
 * an NSNull value anf this value gets accessed by the progran, runtime exceptions about unrecognized
 * selectors will be thrown. This function ensures that when accessing the objects of an NSDictionary,
 * null values will be returned as "nil" instead.
 *
 *	@param 	key The key to access the value.
 *
 *	@return	The value to return, or nil in case where the original JSON is 'null'
 */
- (id)jsonFixedValueForKey:(NSString *)key;


@end
