/*
 * Copyright 2013-2016 Netherlands Forensic Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.minvenj.nfi.ddrx.data;

import java.math.BigInteger;
import java.util.HashMap;

public class ValueStore {
  
  private static final ValueStore _instance = new ValueStore();
  private final HashMap<String,BigInteger> _perm;
  private final HashMap<String,BigInteger> _temp;
  
  private ValueStore() {
      _perm = new HashMap<String,BigInteger>();
      _temp = new HashMap<String,BigInteger>();
  }
  
  public static ValueStore getInstance() {
    return _instance;
  }
  
  public void put(String name, BigInteger value) {
      _temp.put(name, value);
  }
  
  public BigInteger get(String name) {
      if (_temp.containsKey(name)) {
          return _temp.get(name);
      }
      return _perm.get(name);
  }
  
  public void revoke(String name) {
      _temp.remove(name);
  }
  
  public void finalize(String name) {
      _perm.put(name, _temp.get(name));
      _temp.remove(name);
  }
  
}
